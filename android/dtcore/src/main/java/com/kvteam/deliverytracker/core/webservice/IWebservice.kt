package com.kvteam.deliverytracker.core.webservice

import com.kvteam.deliverytracker.core.webservice.viewmodels.ResponseBase
import java.lang.reflect.Type

interface IWebservice {
    fun get(
            url: String,
            withToken: Boolean = false): RawNetworkResult

    fun <T : ResponseBase> get(
            url: String,
            responseType: Type,
            withToken: Boolean = false): NetworkResult<T>

    fun post(
            url: String,
            content: Any?,
            withToken: Boolean = false): RawNetworkResult

    fun <T : ResponseBase> post(
            url: String,
            content: Any?,
            responseType: Type,
            withToken: Boolean = false): NetworkResult<T>
}