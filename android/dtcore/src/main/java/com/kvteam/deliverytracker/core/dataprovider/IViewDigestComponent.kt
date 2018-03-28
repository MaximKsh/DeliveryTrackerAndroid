package com.kvteam.deliverytracker.core.dataprovider

import com.kvteam.deliverytracker.core.models.ViewDigest

interface IViewDigestComponent {
    fun getDigest(viewGroup: String, getMode: DataProviderGetMode) : Map<String, ViewDigest>

    suspend fun getDigestAsync(viewGroup: String, getMode: DataProviderGetMode) : Map<String, ViewDigest>

    fun invalidate(viewGroup: String? = null)
}