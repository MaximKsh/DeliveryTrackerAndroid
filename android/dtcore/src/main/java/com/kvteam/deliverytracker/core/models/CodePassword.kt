package com.kvteam.deliverytracker.core.models

import java.io.Serializable

data class CodePassword(
        var code: String? = null,
        var password: String? = null
) : Serializable