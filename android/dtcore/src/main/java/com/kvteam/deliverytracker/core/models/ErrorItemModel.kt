package com.kvteam.deliverytracker.core.models

data class ErrorItemModel(
        var code: String,
        var message: String,
        var info: Map<String, String>)