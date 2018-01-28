package com.kvteam.deliverytracker.core.webservice

import com.kvteam.deliverytracker.core.models.User
import com.kvteam.deliverytracker.core.webservice.viewmodels.UserRequest
import com.kvteam.deliverytracker.core.webservice.viewmodels.UserResponse
import java.util.*

class UserWebservice(private val webservice: IWebservice) : IUserWebservice {

    private val userBaseUrl = "/api/user"

    override fun get(id: UUID): NetworkResult<UserResponse> {
        val result = webservice.get<UserResponse>(
                "$userBaseUrl/get?id=$id",
                UserResponse::class.java,
                true)
        return result
    }

    override fun get(code: String): NetworkResult<UserResponse> {
        val result = webservice.get<UserResponse>(
                "$userBaseUrl/get?code=$code",
                UserResponse::class.java,
                true)
        return result
    }

    override fun edit(id: UUID, newData: User): NetworkResult<UserResponse> {
        val request = UserRequest()
        request.user = newData.copy(id = id)

        val result = webservice.post<UserResponse>(
                "$userBaseUrl/edit",
                request,
                UserResponse::class.java,
                true)
        return result
    }

    override fun delete(id: UUID): NetworkResult<UserResponse> {
        val request = UserRequest()
        request.id = id

        val result = webservice.post<UserResponse>(
                "$userBaseUrl/delete",
                request,
                UserResponse::class.java,
                true)
        return result
    }

    override fun delete(code: String): NetworkResult<UserResponse> {
        val request = UserRequest()
        request.code = code

        val result = webservice.post<UserResponse>(
                "$userBaseUrl/delete",
                request,
                UserResponse::class.java,
                true)
        return result
    }

}