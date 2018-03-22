package com.kvteam.deliverytracker.core.dataprovider

interface IViewComponent {
    suspend fun getViewResultAsync(viewGroup: String,
                                   view: String,
                                   arguments: Map<String, Any>? = null,
                                   mode: DataProviderGetMode = DataProviderGetMode.FORCE_WEB): DataProviderViewResult

}