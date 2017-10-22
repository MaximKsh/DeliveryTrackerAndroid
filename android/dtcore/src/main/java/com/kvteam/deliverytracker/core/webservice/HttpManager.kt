package com.kvteam.deliverytracker.core.webservice

import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import java.io.IOException
import java.lang.reflect.Type

class HttpManager: IHttpManager {
    private val client = OkHttpClient()

    override fun post(
            url: String,
            content: String?,
            headers: Map<String, String>,
            mediaType: String): NetworkResponse<String> {
        val body = content ?: ""
        val parsedMediaType = MediaType.parse(mediaType) ?: return NetworkResponse()

        val request = Request.Builder().apply {
            url(url)
            post(RequestBody.create(parsedMediaType, body))
            for ((key, value) in headers) {
                addHeader(key, value)
            }
        }.build()
        return execute(request)
    }

    override fun get(
            url: String,
            headers: Map<String, String>): NetworkResponse<String> {
        val request = Request.Builder().apply {
            url(url)
            get()
            for ((key, value) in headers) {
                addHeader(key, value)
            }
        }.build()
        return execute(request)
    }

    private fun execute(request: Request): NetworkResponse<String> {
        return try {
            val response = client.newCall(request).execute()
            NetworkResponse(
                    fetched = true,
                    statusCode = response.code(),
                    responseEntity = response.body()?.string() ?: "")
        } catch (e: IOException) {
            NetworkResponse()
        }
    }

}