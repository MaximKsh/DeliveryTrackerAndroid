package com.kvteam.deliverytracker.core.webservice

import java.lang.reflect.Type

interface IWebservice {
    fun <T : Any> get(
            url: String,
            responseType: Type,
            withToken: Boolean = false): NetworkResponse<T>

    fun <T : Any> post(
            url: String,
            content: Any?,
            responseType: Type,
            withToken: Boolean = false): NetworkResponse<T>
}