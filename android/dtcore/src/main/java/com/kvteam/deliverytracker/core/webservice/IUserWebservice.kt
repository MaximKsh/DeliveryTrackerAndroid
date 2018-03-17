package com.kvteam.deliverytracker.core.webservice

import com.kvteam.deliverytracker.core.models.User
import com.kvteam.deliverytracker.core.webservice.viewmodels.UserResponse
import java.util.*

interface IUserWebservice {
    suspend fun getAsync(id: UUID) : NetworkResult<UserResponse>
    suspend fun getAsync(code: String) : NetworkResult<UserResponse>
    suspend fun editAsync(id: UUID, newData: User)  : NetworkResult<UserResponse>
    suspend fun deleteAsync(id: UUID) : NetworkResult<UserResponse>
    suspend fun deleteAsync(code: String) : NetworkResult<UserResponse>
}