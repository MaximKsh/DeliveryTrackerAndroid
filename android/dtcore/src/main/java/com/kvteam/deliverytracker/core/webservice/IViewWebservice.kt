package com.kvteam.deliverytracker.core.webservice

import com.kvteam.deliverytracker.core.webservice.viewmodels.ViewResponse

interface IViewWebservice {
    suspend fun getGroupsAsync(): NetworkResult<ViewResponse>

    suspend fun getViewsAsync(viewGroup: String): NetworkResult<ViewResponse>

    suspend fun getDigestAsync(viewGroup: String): NetworkResult<ViewResponse>

    suspend fun getViewResultAsync(viewGroup: String, view: String, arguments: Map<String, Any>? = null): NetworkResult<ViewResponse>

}