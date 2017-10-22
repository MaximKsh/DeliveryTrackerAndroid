package com.kvteam.deliverytracker.core.webservice

import java.lang.reflect.Type

interface IHttpManager {
    fun get(
            url: String,
            headers: Map<String, String>): NetworkResponse<String>

    fun post(
            url: String,
            content: String?,
            headers: Map<String, String>,
            mediaType: String): NetworkResponse<String>
}