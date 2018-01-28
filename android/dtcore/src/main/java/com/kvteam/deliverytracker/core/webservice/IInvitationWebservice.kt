package com.kvteam.deliverytracker.core.webservice

import com.kvteam.deliverytracker.core.models.User
import com.kvteam.deliverytracker.core.webservice.viewmodels.InvitationResponse

interface IInvitationWebservice  {
    fun create(preliminaryUserData: User): NetworkResult<InvitationResponse>
    fun get(code: String): NetworkResult<InvitationResponse>
    fun delete(code: String): NetworkResult<InvitationResponse>
}