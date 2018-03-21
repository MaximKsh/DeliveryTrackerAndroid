package com.kvteam.deliverytracker.core.dataprovider

import com.kvteam.deliverytracker.core.models.ViewDigest
import com.kvteam.deliverytracker.core.webservice.IViewWebservice
import kotlinx.coroutines.experimental.async
class ViewDigestComponent (
        private val container: ViewDigestContainer,
        private val viewWebservice: IViewWebservice
) : IViewDigestComponent {

    override suspend fun getDigestAsync(
            viewGroup: String,
            getMode: DataProviderGetMode): Map<String, ViewDigest> = async {
        when(getMode) {
            DataProviderGetMode.FORCE_WEB -> getForceWebAsync(viewGroup)
            DataProviderGetMode.FORCE_CACHE -> getForceCache(viewGroup)
            DataProviderGetMode.PREFER_WEB -> getPreferWebAsync(viewGroup)
            DataProviderGetMode.PREFER_CACHE -> getPreferCacheAsync(viewGroup)
            DataProviderGetMode.DIRTY -> throw IllegalArgumentException()
        }
    }.await()

    private suspend fun getForceWebAsync(viewGroup: String) : Map<String, ViewDigest> {
        val result = viewWebservice.getDigestAsync(viewGroup)
        if(!result.success) {
            throw NetworkException(result)
        }
        val digest = result.entity?.digest!!
        container.putViewDigest(viewGroup, digest)
        return digest
    }

    private fun getForceCache(viewGroup: String) : Map<String, ViewDigest> {
        return container.getViewDigest(viewGroup) ?: throw CacheException()
    }

    private suspend fun getPreferWebAsync(viewGroup: String) : Map<String, ViewDigest> {
        return try {
            getForceWebAsync(viewGroup)
        } catch (e: NetworkException) {
            getForceCache(viewGroup)
        }
    }

    private suspend fun getPreferCacheAsync(viewGroup: String) : Map<String, ViewDigest> {
        return try {
            getForceCache(viewGroup)
        } catch (e: CacheException) {
            getForceWebAsync(viewGroup)
        }
    }
}