package com.kvteam.deliverytracker.core.webservice

import com.kvteam.deliverytracker.core.common.EMPTY_STRING
import com.kvteam.deliverytracker.core.webservice.viewmodels.ViewResponse
import java.net.URLEncoder

class ViewWebservice(private val webservice: IWebservice) : IViewWebservice {

    private val viewBaseUrl = "/api/view"

    override suspend fun getGroupsAsync(): NetworkResult<ViewResponse> {
        val result = webservice.getAsync<ViewResponse>(
                "$viewBaseUrl/groups",
                ViewResponse::class.java,
                true)
        return result
    }

    override suspend fun getViewsAsync(viewGroup: String): NetworkResult<ViewResponse> {
        val result = webservice.getAsync<ViewResponse>(
                "$viewBaseUrl/$viewGroup/views",
                ViewResponse::class.java,
                true)
        return result
    }

    override suspend fun getDigestAsync(viewGroup: String): NetworkResult<ViewResponse> {
        val result = webservice.getAsync<ViewResponse>(
                "$viewBaseUrl/$viewGroup/digest",
                ViewResponse::class.java,
                true)
        return result
    }

    override suspend fun getViewResultAsync(
            viewGroup: String,
            view: String,
            arguments: Map<String, Any>?): NetworkResult<ViewResponse> {

        val getArguments = arguments
                ?.asIterable()
                ?.joinToString(
                        prefix = "?",
                        separator = "&",
                        transform = { "${encode(it.key)}=${encode(it.value)}" })
        val result = webservice.getAsync<ViewResponse>(
                "$viewBaseUrl/$viewGroup/$view${getArguments ?: EMPTY_STRING}",
                ViewResponse::class.java,
                true)
        return result
    }

    private fun encode(str: Any): String{
        return URLEncoder.encode(str.toString(), "UTF-8")
    }

}