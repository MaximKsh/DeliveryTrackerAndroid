package com.kvteam.deliverytracker.core.webservice

import com.kvteam.deliverytracker.core.models.User
import com.kvteam.deliverytracker.core.webservice.viewmodels.InvitationResponse
import java.util.*

interface IInvitationWebservice  {
    suspend fun createAsync(preliminaryUserData: User): NetworkResult<InvitationResponse>
    suspend fun getAsync(code: String): NetworkResult<InvitationResponse>
    suspend fun getAsync(id: UUID): NetworkResult<InvitationResponse>
    suspend fun deleteAsync(code: String): NetworkResult<InvitationResponse>
    suspend fun deleteAsync(id: UUID): NetworkResult<InvitationResponse>
}