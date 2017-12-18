package com.kvteam.deliverytracker.core.webservice

import android.content.Context
import android.text.TextUtils
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.kvteam.deliverytracker.core.R
import com.kvteam.deliverytracker.core.models.ErrorListModel
import com.kvteam.deliverytracker.core.session.ISession
import com.kvteam.deliverytracker.core.session.getAuthorizationHeaders
import java.lang.reflect.Type

class Webservice(context: Context,
                 private val session: ISession,
                 private val httpManager: IHttpManager) : IWebservice {
    // gson is thread-safe
    private val gson = Gson()

    private var baseUrl: String = context.getString(R.string.Core_WebserviceUrl)

    override fun get(
            url: String,
            withToken: Boolean): NetworkResponse<String> {
        var headers =
                if(withToken) getAuthorizationHeaders(session) ?: return NetworkResponse()
                else mapOf()
        var result = httpManager.get(baseUrl + url, headers)

        // Если дали токен, но сервер вернул unauthorized, может быть токен просрочен
        if(withToken && result.statusCode == 401) {
            session.invalidateToken()
            headers = getAuthorizationHeaders(session) ?: return NetworkResponse()
            result = httpManager.get(baseUrl + url, headers)
        }
        return processResponse(result)
    }

    override fun <T : Any> get(
            url: String,
            responseType: Type,
            withToken: Boolean): NetworkResponse<T> {
        var headers =
                if(withToken) getAuthorizationHeaders(session) ?: return NetworkResponse()
                else mapOf()
        var result = httpManager.get(baseUrl + url, headers)

        // Если дали токен, но сервер вернул unauthorized, может быть токен просрочен
        if(withToken && result.statusCode == 401) {
            session.invalidateToken()
            headers = getAuthorizationHeaders(session) ?: return NetworkResponse()
            result = httpManager.get(baseUrl + url, headers)
        }
        return processResponse(result, responseType)
    }

    override fun post(
            url: String,
            content: Any?,
            withToken: Boolean): NetworkResponse<String> {
        var headers =
                if(withToken) getAuthorizationHeaders(session) ?: return NetworkResponse()
                else mapOf()

        val body = if(content != null) gson.toJson(content) else ""
        var result = httpManager.post(
                baseUrl + url,
                body,
                headers,
                "application/json")
        // Если дали токен, но сервер вернул unauthorized, может быть токен просрочен
        if(withToken && result.statusCode == 401) {
            session.invalidateToken()
            headers = getAuthorizationHeaders(session) ?: return NetworkResponse()
            result = httpManager.post(
                    baseUrl + url,
                    body,
                    headers,
                    "application/json")
        }
        return processResponse(result)
    }

    override fun <T : Any> post(
            url: String,
            content: Any?,
            responseType: Type,
            withToken: Boolean): NetworkResponse<T> {
        var headers =
                if(withToken) getAuthorizationHeaders(session) ?: return NetworkResponse()
                else mapOf()

        val body = if(content != null) gson.toJson(content) else ""
        var result = httpManager.post(
                baseUrl + url,
                body,
                headers,
                "application/json")
        // Если дали токен, но сервер вернул unauthorized, может быть токен просрочен
        if(withToken && result.statusCode == 401) {
            session.invalidateToken()
            headers = getAuthorizationHeaders(session) ?: return NetworkResponse()
            result = httpManager.post(
                    baseUrl + url,
                    body,
                    headers,
                    "application/json")
        }
        return processResponse(result, responseType)
    }

    private fun processResponse(response: NetworkResponse<String>): NetworkResponse<String> {
        if(!response.fetched) {
            return NetworkResponse(
                    fetched = response.fetched,
                    statusCode = response.statusCode,
                    errorList = response.errorList)
        }

        return try {
            if(response.statusCode in 200..299) {
                response
            } else {
                val errors = gson.fromJson<ErrorListModel>(
                        response.responseEntity,
                        ErrorListModel::class.java)
                NetworkResponse(
                        fetched = true,
                        statusCode = response.statusCode,
                        errorList = errors)
            }
        } catch (e: JsonSyntaxException) {
            NetworkResponse(
                    fetched = true,
                    statusCode = response.statusCode,
                    errorList = ErrorListModel(listOf(invalidResponseBody())))
        }
    }

    private fun <T : Any> processResponse(
            response: NetworkResponse<String>,
            responseType: Type): NetworkResponse<T> {
        if(!response.fetched
                || TextUtils.isEmpty(response.responseEntity)) {
            return NetworkResponse(
                    fetched = response.fetched,
                    statusCode = response.statusCode,
                    errorList = response.errorList)
        }

        return try {
            if(response.statusCode in 200..299) {
                val obj = gson.fromJson<T>(response.responseEntity, responseType)
                NetworkResponse(
                        fetched = true,
                        statusCode = response.statusCode,
                        responseEntity = obj)
            } else {
                val errors = gson.fromJson<ErrorListModel>(
                        response.responseEntity,
                        ErrorListModel::class.java)
                NetworkResponse(
                        fetched = true,
                        statusCode = response.statusCode,
                        errorList = errors)
            }
        } catch (e: JsonSyntaxException) {
            NetworkResponse(
                    fetched = true,
                    statusCode = response.statusCode,
                    errorList = ErrorListModel(listOf(invalidResponseBody())))
        }
    }
}