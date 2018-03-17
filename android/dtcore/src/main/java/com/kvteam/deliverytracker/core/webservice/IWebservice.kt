package com.kvteam.deliverytracker.core.webservice

import com.kvteam.deliverytracker.core.webservice.viewmodels.ResponseBase
import java.lang.reflect.Type

interface IWebservice {
    suspend fun getAsync(
            url: String,
            withToken: Boolean = false): RawNetworkResult

    suspend fun <T : ResponseBase> getAsync(
            url: String,
            responseType: Type,
            withToken: Boolean = false): NetworkResult<T>

    suspend fun postAsync(
            url: String,
            content: Any?,
            withToken: Boolean = false): RawNetworkResult

    suspend fun <T : ResponseBase> postAsync(
            url: String,
            content: Any?,
            responseType: Type,
            withToken: Boolean = false): NetworkResult<T>
}