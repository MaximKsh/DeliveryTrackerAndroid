package com.kvteam.deliverytracker.core.webservice

import android.content.Context
import android.text.TextUtils
import com.google.gson.JsonSyntaxException
import com.kvteam.deliverytracker.core.R
import com.kvteam.deliverytracker.core.common.IDeliveryTrackerGsonProvider
import com.kvteam.deliverytracker.core.session.ISession
import com.kvteam.deliverytracker.core.webservice.viewmodels.ResponseBase
import kotlinx.coroutines.experimental.async
import java.lang.reflect.Type

class Webservice(
                 gsonProvider: IDeliveryTrackerGsonProvider,
                 context: Context,
                 private val session: ISession,
                 private val httpManager: IHttpManager) : IWebservice {
    // gson is thread-safe
    private val gson = gsonProvider.gson

    private var baseUrl: String = context.getString(R.string.Core_WebserviceUrl)

    override suspend fun getAsync(
            url: String,
            withToken: Boolean): RawNetworkResult = async{
        val result = doubleTimeRequest(session, withToken) {
            httpManager.get(baseUrl + url, it)
        }
        return@async result
    }.await()

    override suspend fun <T : ResponseBase> getAsync(
            url: String,
            responseType: Type,
            withToken: Boolean): NetworkResult<T> = async {

        val result = doubleTimeRequest(session, withToken) {
            httpManager.get(baseUrl + url, it)
        }
        return@async processResponse<T>(result, responseType)
    }.await()

    override suspend fun postAsync(
            url: String,
            content: Any?,
            withToken: Boolean): RawNetworkResult = async {
        val body = if(content != null) gson.toJson(content) else ""
        val result = doubleTimeRequest(session, withToken) {
            httpManager.post(
                    baseUrl + url,
                    body,
                    it,
                    "application/json")
        }
        return@async result
    }.await()

    override suspend fun <T : ResponseBase> postAsync(
            url: String,
            content: Any?,
            responseType: Type,
            withToken: Boolean): NetworkResult<T> = async {
        val body = if(content != null) gson.toJson(content) else ""
        val result = doubleTimeRequest(session, withToken) {
            httpManager.post(
                    baseUrl + url,
                    body,
                    it,
                    "application/json")
        }
        return@async processResponse<T>(result, responseType)
    }.await()

    private fun <T : ResponseBase> processResponse(
            result: RawNetworkResult,
            responseType: Type): NetworkResult<T> {
        if(!result.fetched
                || TextUtils.isEmpty(result.entity)) {
            return NetworkResult.create(result)
        }

        return try {
            val obj = gson.fromJson<T>(result.entity, responseType)
            NetworkResult.create(result, obj)
        } catch (e: JsonSyntaxException) {
            NetworkResult.create(result)
        }
    }
}