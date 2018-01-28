package com.kvteam.deliverytracker.core.webservice

interface IHttpManager {
    fun get(
            url: String,
            headers: Map<String, String>): RawNetworkResult

    fun post(
            url: String,
            content: String?,
            headers: Map<String, String>,
            mediaType: String): RawNetworkResult
}