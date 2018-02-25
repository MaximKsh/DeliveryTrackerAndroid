package com.kvteam.deliverytracker.core.session

import com.kvteam.deliverytracker.core.models.CodePassword
import com.kvteam.deliverytracker.core.models.User
import com.kvteam.deliverytracker.core.webservice.NetworkResult
import com.kvteam.deliverytracker.core.webservice.viewmodels.AccountResponse
import java.util.*

interface ISession {
    fun getToken(): String?
    fun invalidateToken()

    fun setTokenExplicitly(token: String) : Boolean
    fun hasAccount(): Boolean
    fun hasAccount(username: String?): Boolean
    fun logout()

    fun login(username: String, password: String): LoginResult
    fun refreshUserInfo(): NetworkResult<AccountResponse>
    fun editUserInfo(userInfo: User): NetworkResult<AccountResponse>
    fun changePassword(old: CodePassword,
                       new: CodePassword) : NetworkResult<AccountResponse>

    val id: UUID?
    val code: String?
    val surname: String?
    val name: String?
    val phoneNumber: String?
    val role: UUID?
}