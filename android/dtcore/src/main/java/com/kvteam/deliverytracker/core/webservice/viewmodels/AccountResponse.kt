package com.kvteam.deliverytracker.core.webservice.viewmodels

import com.kvteam.deliverytracker.core.models.User

data class AccountResponse(
        var user: User? = null,
        var token: String? = null
): ResponseBase()