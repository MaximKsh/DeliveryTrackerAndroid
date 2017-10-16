package com.kvteam.deliverytracker.core.webservice

import java.lang.reflect.Type

interface IWebservice {
    fun <T : Any> get(
            url: String,
            responseType: Type,
            onSuccess: (T) -> Unit,
            onError: (ErrorListModel) -> Unit)

    fun <T : Any> post(
            url: String,
            content: Any?,
            responseType: Type,
            onSuccess: (T) -> Unit,
            onError: (ErrorListModel) -> Unit)
}