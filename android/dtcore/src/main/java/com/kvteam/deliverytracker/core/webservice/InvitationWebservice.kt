package com.kvteam.deliverytracker.core.webservice

import com.kvteam.deliverytracker.core.models.User
import com.kvteam.deliverytracker.core.webservice.viewmodels.InvitationRequest
import com.kvteam.deliverytracker.core.webservice.viewmodels.InvitationResponse
import java.util.*

class InvitationWebservice(private val webservice: IWebservice): IInvitationWebservice {


    private val invitationBaseUrl = "/api/invitation"

    override suspend fun createAsync(preliminaryUserData: User) : NetworkResult<InvitationResponse> {
        if (preliminaryUserData.role == null) {
            throw IllegalArgumentException("codePassword.role")
        }

        val request = InvitationRequest()
        request.user = preliminaryUserData
        return webservice.postAsync(
                "$invitationBaseUrl/create",
                request,
                InvitationResponse::class.java,
                true)
    }

    override suspend fun getAsync(code: String) : NetworkResult<InvitationResponse>{
        return webservice.getAsync(
                "$invitationBaseUrl/get?code=$code",
                InvitationResponse::class.java,
                true)
    }

    suspend override fun getAsync(id: UUID): NetworkResult<InvitationResponse> {
        return webservice.getAsync(
                "$invitationBaseUrl/get?id=$id",
                InvitationResponse::class.java,
                true)    }

    override suspend fun deleteAsync(code: String): NetworkResult<InvitationResponse> {
        val request = InvitationRequest()
        request.code = code
        return webservice.postAsync(
                "$invitationBaseUrl/delete",
                request,
                InvitationResponse::class.java,
                true)
    }

    override suspend fun deleteAsync(id: UUID): NetworkResult<InvitationResponse> {
        val request = InvitationRequest()
        request.id = id
        return webservice.postAsync(
                "$invitationBaseUrl/delete",
                request,
                InvitationResponse::class.java,
                true)    }

}