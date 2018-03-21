package com.kvteam.deliverytracker.core.dataprovider

import java.util.*

interface IViewComponent {
    suspend fun getViewResultAsync(viewGroup: String,
                                   view: String,
                                   arguments: Map<String, Any>? = null,
                                   mode: DataProviderGetMode = DataProviderGetMode.FORCE_WEB): List<UUID>

}