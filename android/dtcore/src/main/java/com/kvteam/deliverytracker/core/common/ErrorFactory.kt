package com.kvteam.deliverytracker.core.common

import com.kvteam.deliverytracker.core.models.Error

fun networkError() =
        Error(NetworkError, "Core_Error_NetworkError")

fun invalidResponseBody() =
        Error(InvalidResponseBody, "Core_Error_InvalidResponseBody")

fun unauthorized() =
        Error(Unauthorized, "Core_Error_Unauthorized")