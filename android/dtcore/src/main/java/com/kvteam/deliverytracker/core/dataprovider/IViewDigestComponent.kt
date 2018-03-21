package com.kvteam.deliverytracker.core.dataprovider

import com.kvteam.deliverytracker.core.models.ViewDigest

interface IViewDigestComponent {
    suspend fun getDigestAsync(viewGroup: String, getMode: DataProviderGetMode) : Map<String, ViewDigest>
}