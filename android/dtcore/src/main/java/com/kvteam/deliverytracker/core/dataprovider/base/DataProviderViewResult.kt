package com.kvteam.deliverytracker.core.dataprovider.base

import java.util.*

data class DataProviderViewResult (
        val viewResult: List<UUID>,
        val origin: DataProviderGetOrigin
)