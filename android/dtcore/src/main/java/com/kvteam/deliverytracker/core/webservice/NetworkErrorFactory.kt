package com.kvteam.deliverytracker.core.webservice

import com.kvteam.deliverytracker.core.models.ErrorItemModel

fun invalidResponseBody() =
        ErrorItemModel(UnknownNetworkError, "InvalidResponseBody", mapOf())