package com.kvteam.deliverytracker.core.webservice.viewmodels

import com.kvteam.deliverytracker.core.models.User
import java.util.*

data class UserRequest(
        var id: UUID? = null,
        var code: String? = null,
        var user: User? = null
) : RequestBase()