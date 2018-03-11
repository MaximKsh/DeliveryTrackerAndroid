package com.kvteam.deliverytracker.core.webservice

import android.content.Context
import android.text.TextUtils
import com.google.gson.JsonSyntaxException
import com.kvteam.deliverytracker.core.R
import com.kvteam.deliverytracker.core.common.buildDefaultGson
import com.kvteam.deliverytracker.core.common.invalidResponseBody
import com.kvteam.deliverytracker.core.session.ISession
import com.kvteam.deliverytracker.core.session.getAuthorizationHeaders
import com.kvteam.deliverytracker.core.webservice.viewmodels.ResponseBase
import java.lang.reflect.Type

class Webservice(context: Context,
                 private val session: ISession,
                 private val httpManager: IHttpManager) : IWebservice {
    // gson is thread-safe
    private val gson = buildDefaultGson()

    private var baseUrl: String = context.getString(R.string.Core_WebserviceUrl)

    override fun get(
            url: String,
            withToken: Boolean): RawNetworkResult {
        var headers =
                if(withToken) getAuthorizationHeaders(session) ?: return RawNetworkResult()
                else mapOf()
        var result = httpManager.get(baseUrl + url, headers)

        // Если дали токен, но сервер вернул forbidden, может быть токен просрочен
        if(withToken && result.statusCode == 403) {
            session.invalidateToken()
            headers = getAuthorizationHeaders(session) ?: return RawNetworkResult()
            result = httpManager.get(baseUrl + url, headers)
        }
        return result
    }

    override fun <T : ResponseBase> get(
            url: String,
            responseType: Type,
            withToken: Boolean): NetworkResult<T> {
        var headers =
                if(withToken) getAuthorizationHeaders(session) ?: return NetworkResult()
                else mapOf()
        var result = httpManager.get(baseUrl + url, headers)

        // Если дали токен, но сервер вернул forbidden, может быть токен просрочен
        if(withToken && result.statusCode == 403) {
            session.invalidateToken()
            headers = getAuthorizationHeaders(session) ?: return NetworkResult()
            result = httpManager.get(baseUrl + url, headers)
        }
        return processResponse(result, responseType)
    }

    override fun post(
            url: String,
            content: Any?,
            withToken: Boolean): RawNetworkResult {
        var headers =
                if(withToken) getAuthorizationHeaders(session) ?: return RawNetworkResult()
                else mapOf()

        val body = if(content != null) gson.toJson(content) else ""
        var result = httpManager.post(
                baseUrl + url,
                body,
                headers,
                "application/json")
        // Если дали токен, но сервер вернул unauthorized, может быть токен просрочен
        if(withToken && result.statusCode == 403) {
            session.invalidateToken()
            headers = getAuthorizationHeaders(session) ?: return RawNetworkResult()
            result = httpManager.post(
                    baseUrl + url,
                    body,
                    headers,
                    "application/json")
        }
        return result
    }

    override fun <T : ResponseBase> post(
            url: String,
            content: Any?,
            responseType: Type,
            withToken: Boolean): NetworkResult<T> {
        var headers =
                if(withToken) getAuthorizationHeaders(session) ?: return NetworkResult()
                else mapOf()

        val body = if(content != null) gson.toJson(content) else ""
        var result = httpManager.post(
                baseUrl + url,
                body,
                headers,
                "application/json")
        // Если дали токен, но сервер вернул forbidden, может быть токен просрочен
        if(withToken && result.statusCode == 403) {
            session.invalidateToken()
            headers = getAuthorizationHeaders(session) ?: return NetworkResult()
            result = httpManager.post(
                    baseUrl + url,
                    body,
                    headers,
                    "application/json")
        }
        return processResponse(result, responseType)
    }

    private fun <T : ResponseBase> processResponse(
            result: RawNetworkResult,
            responseType: Type): NetworkResult<T> {
        if(!result.fetched
                || TextUtils.isEmpty(result.entity)) {
            return NetworkResult(
                    fetched = result.fetched,
                    statusCode = result.statusCode,
                    errors = result.errors)
        }

        return try {
            val obj = gson.fromJson<T>(result.entity, responseType)
            NetworkResult(
                    obj,
                    fetched = true,
                    statusCode = result.statusCode)
        } catch (e: JsonSyntaxException) {
            NetworkResult(
                    fetched = true,
                    statusCode = result.statusCode,
                    errors = listOf(invalidResponseBody()))
        }
    }
}