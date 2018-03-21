package com.kvteam.deliverytracker.core.webservice.viewmodels

import com.kvteam.deliverytracker.core.models.User
import java.util.*

data class InvitationRequest(
        var id: UUID? = null,
        var user: User? = null,
        var code: String?  = null
) : RequestBase()