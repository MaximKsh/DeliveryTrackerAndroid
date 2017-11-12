package com.kvteam.deliverytracker.core.models

data class TokenModel(
        var user: UserModel,
        var token: String)