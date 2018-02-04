package com.kvteam.deliverytracker.core.webservice

import com.kvteam.deliverytracker.core.models.User
import com.kvteam.deliverytracker.core.webservice.viewmodels.InvitationRequest
import com.kvteam.deliverytracker.core.webservice.viewmodels.InvitationResponse

class InvitationWebservice(private val webservice: IWebservice): IInvitationWebservice {

    private val invitationBaseUrl = "/api/invitation"


    override fun create(preliminaryUserData: User) : NetworkResult<InvitationResponse> {
        if (preliminaryUserData.role == null) {
            throw IllegalArgumentException("codePassword.role")
        }

        val request = InvitationRequest()
        request.user = preliminaryUserData
        val result = webservice.post<InvitationResponse>(
                "$invitationBaseUrl/create",
                request,
                InvitationResponse::class.java,
                true)
        return result
    }

    override fun get(code: String) : NetworkResult<InvitationResponse>{
        val result = webservice.get<InvitationResponse>(
                "$invitationBaseUrl/get?code=$code",
                InvitationResponse::class.java,
                true)
        return result
    }

    override fun delete(code: String): NetworkResult<InvitationResponse> {
        val request = InvitationRequest()
        request.code = code
        val result = webservice.post<InvitationResponse>(
                "$invitationBaseUrl/delete",
                request,
                InvitationResponse::class.java,
                true)
        return result
    }

}