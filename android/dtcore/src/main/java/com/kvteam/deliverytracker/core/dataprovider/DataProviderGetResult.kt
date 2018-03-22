package com.kvteam.deliverytracker.core.dataprovider

import com.kvteam.deliverytracker.core.models.ModelBase

data class DataProviderGetResult <out T : ModelBase> (
    val entry: T,
    val origin: DataProviderGetOrigin
)