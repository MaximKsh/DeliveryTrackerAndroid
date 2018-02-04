package com.kvteam.deliverytracker.core.webservice

import com.kvteam.deliverytracker.core.models.User
import com.kvteam.deliverytracker.core.webservice.viewmodels.UserResponse
import java.util.*

interface IUserWebservice {
    fun get(id: UUID) : NetworkResult<UserResponse>
    fun get(code: String) : NetworkResult<UserResponse>
    fun edit(id: UUID, newData: User)  : NetworkResult<UserResponse>
    fun delete(id: UUID) : NetworkResult<UserResponse>
    fun delete(code: String) : NetworkResult<UserResponse>
}