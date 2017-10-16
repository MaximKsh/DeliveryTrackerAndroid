package com.kvteam.deliverytracker.core.webservice

import android.content.Context
import com.android.volley.Request
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.kvteam.deliverytracker.core.R
import com.android.volley.toolbox.StringRequest
import com.google.gson.JsonSyntaxException
import java.lang.reflect.Type

class Webservice constructor(var context: Context) : IWebservice {
    // gson is thread-safe
    val gson = Gson()

    override fun <T : Any> get(
            url: String,
            responseType: Type,
            onSuccess: (T) -> Unit,
            onError: (ErrorListModel) -> Unit) {
        this.fetch(
                Request.Method.GET,
                url,
                null,
                responseType,
                onSuccess,
                onError)
    }

    override fun <T : Any> post(
            url: String,
            content: Any?,
            responseType: Type,
            onSuccess: (T) -> Unit,
            onError: (ErrorListModel) -> Unit) {
        this.fetch(
                Request.Method.POST,
                url,
                content,
                responseType,
                onSuccess,
                onError)
    }

    private fun <T : Any> fetch(
            method: Int,
            url: String,
            content: Any?,
            responseType: Type,
            onSuccess: (T) -> Unit,
            onError: (ErrorListModel) -> Unit) {
        val rawContent = if (content != null) this.gson.toJson(content) else ""

        val baseUrl = context.getString(R.string.Core_WebserviceUrl)
        val queue = Volley.newRequestQueue(context)
        val stringRequest = object : StringRequest(
                method,
                "$baseUrl$url",
                { response -> processResponse(response, responseType, onSuccess, onError) },
                { error -> tryParseError(error.networkResponse?.data?.toString(), onError) }) {
            override fun getBody() = rawContent.toByteArray()

            override fun getBodyContentType() = "application/json"

            override fun getHeaders(): Map<String, String> = mapOf(
                    "User-Agent" to "DeliveryTrackerAndroid")
        }

        queue.add(stringRequest)

    }

    private fun <T : Any> processResponse(response: String,
                                          responseType: Type,
                                          onSuccess: (T) -> Unit,
                                          onError: (ErrorListModel) -> Unit) {
        try{
            val obj = this.gson.fromJson<T>(response, responseType)
            onSuccess(obj)
        } catch (e: JsonSyntaxException) {
            tryParseError(response, onError)
        }
    }

    private fun tryParseError(rawError: String?,
                              onError: (ErrorListModel) -> Unit) {
        if(rawError == null) {
            onError(ErrorListModel(listOf()))
        }

        try {
            val errList = this.gson.fromJson<ErrorListModel>(
                    rawError,
                    ErrorListModel::class.java)
            onError(errList)
        } catch (e2: JsonSyntaxException) {
            onError(ErrorListModel(listOf()))
        }
    }

}