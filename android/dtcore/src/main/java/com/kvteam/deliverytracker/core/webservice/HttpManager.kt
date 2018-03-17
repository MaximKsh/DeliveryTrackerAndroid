package com.kvteam.deliverytracker.core.webservice

import com.kvteam.deliverytracker.core.common.EMPTY_STRING
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import java.io.IOException

class HttpManager: IHttpManager {
    private val client = OkHttpClient()

    override fun post(
            url: String,
            content: String?,
            headers: Map<String, String>,
            mediaType: String): RawNetworkResult {
        val body = content ?: EMPTY_STRING
        val parsedMediaType = MediaType.parse(mediaType) ?: return RawNetworkResult()

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
            headers: Map<String, String>): RawNetworkResult {
        val request = Request.Builder().apply {
            url(url)
            get()
            for ((key, value) in headers) {
                addHeader(key, value)
            }
        }.build()
        return execute(request)
    }

    private fun execute(request: Request): RawNetworkResult {
        return try {
            val response = client.newCall(request).execute()
            val result = RawNetworkResult(
                    fetched = true,
                    statusCode = response.code())
            result.entity = response.body()?.string() ?: EMPTY_STRING
            result
        } catch (e: IOException) {
            RawNetworkResult(fetched = false)
        }
    }
}