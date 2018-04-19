package com.kvteam.deliverytracker.core.dataprovider.base


import com.kvteam.deliverytracker.core.models.ViewDigest

data class DataProviderViewDigestResult (
        val digest: Map<String, ViewDigest>,
        val origin: DataProviderGetOrigin
)