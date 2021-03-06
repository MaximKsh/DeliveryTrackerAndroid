package com.kvteam.deliverytracker.core.dataprovider.base

import com.kvteam.deliverytracker.core.models.ModelBase
import com.kvteam.deliverytracker.core.webservice.IViewWebservice
import com.kvteam.deliverytracker.core.webservice.NetworkResult
import com.kvteam.deliverytracker.core.webservice.viewmodels.ViewResponse
import kotlinx.coroutines.experimental.async

abstract class BaseViewComponent <out T : ModelBase> (
        private val viewWebservice: IViewWebservice,
        private val dataContainer: IDataContainer<T>
) : IViewComponent {

    protected abstract fun entryFactory() : T

    protected open fun transformViewResultToEntries(viewResult: NetworkResult<ViewResponse>) : List<T> {
        return viewResult.entity?.viewResult?.map {
            val entry = entryFactory()
            entry.fromMap(it)
            entry
        }?.toList()!!
    }

    override fun getViewResult(
            viewGroup: String,
            view: String,
            arguments: Map<String, Any>?,
            mode: DataProviderGetMode): DataProviderViewResult {
        if(mode != DataProviderGetMode.FORCE_CACHE) {
            throw ActionNotSupportedException()
        }
        return getForceCacheViewResult(viewGroup, view, arguments)
    }

    override suspend fun getViewResultAsync(
            viewGroup: String,
            view: String,
            arguments: Map<String, Any>?,
            mode: DataProviderGetMode): DataProviderViewResult = async{
        when(mode) {
            DataProviderGetMode.FORCE_WEB -> getForceWebViewResultAsync(viewGroup, view, arguments)
            DataProviderGetMode.FORCE_CACHE -> getForceCacheViewResult(viewGroup, view, arguments)
            DataProviderGetMode.PREFER_WEB -> getPreferWebAsync(viewGroup, view, arguments)
            DataProviderGetMode.PREFER_CACHE -> getPreferCacheAsync(viewGroup, view, arguments)
            DataProviderGetMode.DIRTY -> throw ActionNotSupportedException()
        }
    }.await()

    override fun invalidate() {
        dataContainer.clearViews()
    }

    override fun invalidate(viewGroup: String, view: String, arguments: Map<String, Any>?) {
        dataContainer.removeView(
                ViewRequestKey(
                        viewGroup,
                        view,
                        arguments))
    }

    private suspend fun getForceWebViewResultAsync(viewGroup: String,
                                                   view: String,
                                                   arguments: Map<String, Any>?) : DataProviderViewResult {
        val viewResult = viewWebservice.getViewResultAsync(viewGroup, view, arguments)
        if(!viewResult.success) {
            throw NetworkException(viewResult)
        }
        val entries = transformViewResultToEntries(viewResult)

        entries.forEach { dataContainer.putEntry(it) }

        val key = ViewRequestKey(
                viewGroup,
                view,
                arguments)
        val ids = entries.map { it.id!! }.toList()
        dataContainer.putView(key, ids)

        return DataProviderViewResult(
                ids,
                DataProviderGetOrigin.WEB)
    }

    private fun getForceCacheViewResult(viewGroup: String,
                                        view: String,
                                        arguments: Map<String, Any>?) : DataProviderViewResult {
        val result = dataContainer.getView(
                ViewRequestKey(
                        viewGroup,
                        view,
                        arguments)) ?: throw CacheException()
        return DataProviderViewResult(
                result,
                DataProviderGetOrigin.CACHE)
    }

    private suspend fun getPreferWebAsync(viewGroup: String,
                                          view: String,
                                          arguments: Map<String, Any>?) : DataProviderViewResult {
        return try {
            getForceWebViewResultAsync(viewGroup, view, arguments)
        } catch (e: NetworkException) {
            getForceCacheViewResult(viewGroup, view, arguments)
        }
    }

    private suspend fun getPreferCacheAsync(viewGroup: String,
                                            view: String,
                                            arguments: Map<String, Any>?) : DataProviderViewResult {
        return try {
            getForceCacheViewResult(viewGroup, view, arguments)
        } catch (e: CacheException) {
            getForceWebViewResultAsync(viewGroup, view, arguments)
        }
    }

}