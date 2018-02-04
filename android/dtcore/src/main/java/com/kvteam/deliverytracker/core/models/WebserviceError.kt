package com.kvteam.deliverytracker.core.models

data class WebserviceError(override var code: String,
                           override var message: String,
                           override var info: Map<String, String> = mapOf()) : IError