package com.kvteam.deliverytracker.core.webservice

import com.kvteam.deliverytracker.core.webservice.viewmodels.ViewResponse

interface IViewWebservice {
    fun getGroups() : NetworkResult<ViewResponse>

    fun getViews(viewGroup: String) : NetworkResult<ViewResponse>

    fun getDigest(viewGroup: String) : NetworkResult<ViewResponse>

    fun getViewResult(viewGroup: String, view: String) : NetworkResult<ViewResponse>

}