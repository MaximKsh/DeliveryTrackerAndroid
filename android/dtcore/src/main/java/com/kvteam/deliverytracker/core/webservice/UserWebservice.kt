package com.kvteam.deliverytracker.core.webservice

import com.kvteam.deliverytracker.core.models.User
import com.kvteam.deliverytracker.core.webservice.viewmodels.UserRequest
import com.kvteam.deliverytracker.core.webservice.viewmodels.UserResponse
import java.util.*

class UserWebservice(private val webservice: IWebservice) : IUserWebservice {

    private val userBaseUrl = "/api/user"

    override suspend fun getAsync(id: UUID): NetworkResult<UserResponse> {
        val result = webservice.getAsync<UserResponse>(
                "$userBaseUrl/get?id=$id",
                UserResponse::class.java,
                true)
        return result
    }

    override suspend fun getAsync(code: String): NetworkResult<UserResponse> {
        val result = webservice.getAsync<UserResponse>(
                "$userBaseUrl/get?code=$code",
                UserResponse::class.java,
                true)
        return result
    }

    override suspend fun editAsync(id: UUID, newData: User): NetworkResult<UserResponse> {
        newData.id = id
        val request = UserRequest()
        request.user = newData

        val result = webservice.postAsync<UserResponse>(
                "$userBaseUrl/edit",
                request,
                UserResponse::class.java,
                true)
        return result
    }

    override suspend fun deleteAsync(id: UUID): NetworkResult<UserResponse> {
        val request = UserRequest()
        request.id = id

        val result = webservice.postAsync<UserResponse>(
                "$userBaseUrl/delete",
                request,
                UserResponse::class.java,
                true)
        return result
    }

    override suspend fun deleteAsync(code: String): NetworkResult<UserResponse> {
        val request = UserRequest()
        request.code = code

        val result = webservice.postAsync<UserResponse>(
                "$userBaseUrl/delete",
                request,
                UserResponse::class.java,
                true)
        return result
    }

}