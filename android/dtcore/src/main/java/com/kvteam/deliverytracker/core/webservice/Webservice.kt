package com.kvteam.deliverytracker.core.webservice

import android.content.Context
import com.google.gson.Gson
import com.kvteam.deliverytracker.core.R
import com.google.gson.JsonSyntaxException
import com.kvteam.deliverytracker.core.session.ISession
import okhttp3.*
import kotlin.io.use
import java.io.IOException
import java.lang.reflect.Type

class Webservice(
        private val session: ISession,
        context: Context) : IWebservice {
    // gson is thread-safe
    private val gson = Gson()
    private val client = OkHttpClient()

    private val mediaType = MediaType.parse("application/json")

    private var baseUrl: String = context.getString(R.string.Core_WebserviceUrl)

    override fun <T : Any> get(
            url: String,
            responseType: Type,
            withToken: Boolean): NetworkResponse<T> {
        val request = Request.Builder().apply {
            url("$baseUrl$url")
            get()
            if(withToken) {
                addHeader("Authorization", "Bearer $session.token")
            }
        }.build()
        return execute(request, responseType)
    }

    override fun <T : Any> post(
            url: String,
            content: Any?,
            responseType: Type,
            withToken: Boolean): NetworkResponse<T> {
        val body = if(content != null) this.gson.toJson(content) else ""
        val request = Request.Builder().apply {
            url("$baseUrl$url")
            post(RequestBody.create(mediaType, body))
            if(withToken) {
                addHeader("Authorization", "Bearer $session.token")
            }
        }.build()
        return execute(request, responseType)
    }

    private fun <T : Any> execute(
            request: Request,
            responseType: Type): NetworkResponse<T> {
        return try {
            val response = client.newCall(request).execute()
            processResponse(response, responseType)
        } catch (e: IOException) {
            NetworkResponse(null, ErrorListModel(listOf(unknownNetworkError())))
        }
    }
    private fun <T : Any> processResponse(
            response: Response,
            responseType: Type): NetworkResponse<T> {
        response.body().use {
            if(it == null) {
                return NetworkResponse(null, null)
            }

            return if(response.isSuccessful) {
                try {
                    val obj = this.gson.fromJson<T>(it.string(), responseType)
                    NetworkResponse(obj, null)
                } catch (e: JsonSyntaxException) {
                    NetworkResponse(null, ErrorListModel(listOf(unknownNetworkError())))
                }
            } else {
                processError(it)
            }
        }
    }

    private fun <T : Any> processError(body: ResponseBody): NetworkResponse<T> {
        return try {
            NetworkResponse(
                    null,
                    this.gson.fromJson<ErrorListModel>(body.string(), ErrorListModel::class.java))
        } catch (e2: JsonSyntaxException) {
            NetworkResponse(null, ErrorListModel(listOf(unknownNetworkError())))
        }
    }

}