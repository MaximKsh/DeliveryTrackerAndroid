package com.kvteam.deliverytracker.core.dataprovider

import com.kvteam.deliverytracker.core.models.ModelBase
import com.kvteam.deliverytracker.core.webservice.IViewWebservice
import kotlinx.coroutines.experimental.async
import java.util.*

abstract class BaseViewComponent <out T : ModelBase> (
        private val viewWebservice: IViewWebservice,
        private val dataContainer: IDataContainer<T>
) : IViewComponent {

    abstract fun entryFactory() : T

    override suspend fun getViewResultAsync(
            viewGroup: String,
            view: String,
            arguments: Map<String, Any>?,
            mode: DataProviderGetMode): List<UUID> = async{
        when(mode) {
            DataProviderGetMode.FORCE_WEB -> getForceWebViewResultAsync(viewGroup, view, arguments)
            DataProviderGetMode.FORCE_CACHE -> getForceCacheViewResult(viewGroup, view, arguments)
            DataProviderGetMode.PREFER_WEB -> getPreferWebAsync(viewGroup, view, arguments)
            DataProviderGetMode.PREFER_CACHE -> getPreferCacheAsync(viewGroup, view, arguments)
            DataProviderGetMode.DIRTY -> throw IllegalArgumentException()
        }
    }.await()

    private suspend fun getForceWebViewResultAsync(viewGroup: String,
                                                   view: String,
                                                   arguments: Map<String, Any>?) : List<UUID> {
        val viewResult = viewWebservice.getViewResultAsync(viewGroup, view, arguments)
        if(!viewResult.success) {
            throw NetworkException(viewResult)
        }
        val entries = viewResult.entity?.viewResult?.map {
            val entry = entryFactory()
            entry.fromMap(it)
            entry
        }?.toList()!!

        entries.forEach { dataContainer.putEntry(it) }

        val key =  ViewRequestKey(viewGroup, view, arguments)
        val ids = entries.map { it.id!! }.toList()
        dataContainer.putView(key, ids)

        return ids
    }

    private fun getForceCacheViewResult(viewGroup: String,
                                        view: String,
                                        arguments: Map<String, Any>?) : List<UUID> {
        return dataContainer.getView(ViewRequestKey(viewGroup, view, arguments)) ?: throw CacheException()
    }

    private suspend fun getPreferWebAsync(viewGroup: String,
                                          view: String,
                                          arguments: Map<String, Any>?) : List<UUID> {
        return try {
            getForceWebViewResultAsync(viewGroup, view, arguments)
        } catch (e: NetworkException) {
            getForceCacheViewResult(viewGroup, view, arguments)
        }
    }

    private suspend fun getPreferCacheAsync(viewGroup: String,
                                            view: String,
                                            arguments: Map<String, Any>?) : List<UUID> {
        return try {
            getForceCacheViewResult(viewGroup, view, arguments)
        } catch (e: CacheException) {
            getForceWebViewResultAsync(viewGroup, view, arguments)
        }
    }

}