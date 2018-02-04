package com.kvteam.deliverytracker.core.webservice

import com.kvteam.deliverytracker.core.webservice.viewmodels.ViewResponse

class ViewWebservice(private val webservice: IWebservice) : IViewWebservice {

    private val viewBaseUrl = "/api/view"

    override fun getGroups(): NetworkResult<ViewResponse> {
        val result = webservice.get<ViewResponse>(
                "$viewBaseUrl/groups",
                ViewResponse::class.java,
                true)
        return result
    }

    override fun getViews(viewGroup: String): NetworkResult<ViewResponse> {
        val result = webservice.get<ViewResponse>(
                "$viewBaseUrl/$viewGroup/views",
                ViewResponse::class.java,
                true)
        return result
    }

    override fun getDigest(viewGroup: String): NetworkResult<ViewResponse> {
        val result = webservice.get<ViewResponse>(
                "$viewBaseUrl/$viewGroup/digest",
                ViewResponse::class.java,
                true)
        return result
    }

    override fun getViewResult(viewGroup: String, view: String): NetworkResult<ViewResponse> {
        val result = webservice.get<ViewResponse>(
                "$viewBaseUrl/$viewGroup/$view",
                ViewResponse::class.java,
                true)
        return result
    }

}