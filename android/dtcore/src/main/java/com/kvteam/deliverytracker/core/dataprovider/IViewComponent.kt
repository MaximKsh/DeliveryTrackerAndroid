package com.kvteam.deliverytracker.core.dataprovider

interface IViewComponent {
    fun getViewResult(viewGroup: String,
                      view: String,
                      arguments: Map<String, Any>? = null,
                      mode: DataProviderGetMode = DataProviderGetMode.FORCE_WEB): DataProviderViewResult

    suspend fun getViewResultAsync(viewGroup: String,
                                   view: String,
                                   arguments: Map<String, Any>? = null,
                                   mode: DataProviderGetMode = DataProviderGetMode.FORCE_WEB): DataProviderViewResult

    fun invalidate()

    fun invalidate(viewGroup: String, view: String, arguments: Map<String, Any>?)
}