package com.kvteam.deliverytracker.core.webservice.viewmodels

import com.kvteam.deliverytracker.core.models.User

data class UserResponse(
        var user: User? = null
) : ResponseBase()