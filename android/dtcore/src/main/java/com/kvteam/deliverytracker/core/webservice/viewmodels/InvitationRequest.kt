package com.kvteam.deliverytracker.core.webservice.viewmodels

import com.kvteam.deliverytracker.core.models.User

data class InvitationRequest(
        var user: User? = null,
        var code: String?  = null
) : RequestBase()