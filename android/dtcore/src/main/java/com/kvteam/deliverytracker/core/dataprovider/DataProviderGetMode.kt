package com.kvteam.deliverytracker.core.dataprovider

enum class DataProviderGetMode {
    FORCE_WEB,
    FORCE_CACHE,
    PREFER_WEB,
    PREFER_CACHE,
    DIRTY
}