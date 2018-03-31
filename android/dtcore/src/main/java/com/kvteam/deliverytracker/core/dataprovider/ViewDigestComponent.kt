package com.kvteam.deliverytracker.core.dataprovider

import com.kvteam.deliverytracker.core.webservice.IViewWebservice
import kotlinx.coroutines.experimental.async

class ViewDigestComponent (
        private val container: ViewDigestContainer,
        private val viewWebservice: IViewWebservice
) : IViewDigestComponent {

    override fun getDigest(
            viewGroup: String,
            getMode: DataProviderGetMode): DataProviderViewDigestResult {
        if(getMode != DataProviderGetMode.FORCE_CACHE) {
            throw ActionNotSupportedException()
        }
        return getForceCache(viewGroup)
    }

    override suspend fun getDigestAsync(
            viewGroup: String,
            getMode: DataProviderGetMode): DataProviderViewDigestResult = async {
        when(getMode) {
            DataProviderGetMode.FORCE_WEB -> getForceWebAsync(viewGroup)
            DataProviderGetMode.FORCE_CACHE -> getForceCache(viewGroup)
            DataProviderGetMode.PREFER_WEB -> getPreferWebAsync(viewGroup)
            DataProviderGetMode.PREFER_CACHE -> getPreferCacheAsync(viewGroup)
            DataProviderGetMode.DIRTY -> throw ActionNotSupportedException()
        }
    }.await()

    override fun invalidate(viewGroup: String?) {
        if(viewGroup == null) {
            container.clearViewDigests()
        } else {
            container.removeViewDigest(viewGroup)
        }
    }

    private suspend fun getForceWebAsync(viewGroup: String) : DataProviderViewDigestResult {
        val result = viewWebservice.getDigestAsync(viewGroup)
        if(!result.success) {
            throw NetworkException(result)
        }
        val digest = result.entity?.digest!!
        container.putViewDigest(viewGroup, digest)
        return DataProviderViewDigestResult(digest, DataProviderGetOrigin.WEB)
    }

    private fun getForceCache(viewGroup: String) : DataProviderViewDigestResult {
        val digest = container.getViewDigest(viewGroup) ?: throw CacheException()
        return DataProviderViewDigestResult(digest, DataProviderGetOrigin.CACHE)
    }

    private suspend fun getPreferWebAsync(viewGroup: String) : DataProviderViewDigestResult {
        return try {
            getForceWebAsync(viewGroup)
        } catch (e: NetworkException) {
            getForceCache(viewGroup)
        }
    }

    private suspend fun getPreferCacheAsync(viewGroup: String) : DataProviderViewDigestResult {
        return try {
            getForceCache(viewGroup)
        } catch (e: CacheException) {
            getForceWebAsync(viewGroup)
        }
    }
}