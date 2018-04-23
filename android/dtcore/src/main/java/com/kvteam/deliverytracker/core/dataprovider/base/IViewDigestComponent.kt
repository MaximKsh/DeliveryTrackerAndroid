package com.kvteam.deliverytracker.core.dataprovider.base

interface IViewDigestComponent {
    fun getDigest(viewGroup: String, getMode: DataProviderGetMode) : DataProviderViewDigestResult

    suspend fun getDigestAsync(viewGroup: String, getMode: DataProviderGetMode) : DataProviderViewDigestResult

    fun invalidate(viewGroup: String? = null)
}