package com.kvteam.deliverytracker.core.webservice.viewmodels

import com.kvteam.deliverytracker.core.models.Invitation

data class InvitationResponse(
    var invitation: Invitation? = null
): ResponseBase()