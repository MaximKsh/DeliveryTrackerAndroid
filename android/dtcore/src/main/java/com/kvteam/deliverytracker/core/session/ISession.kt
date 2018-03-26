package com.kvteam.deliverytracker.core.session

import com.kvteam.deliverytracker.core.models.CodePassword
import com.kvteam.deliverytracker.core.models.User
import com.kvteam.deliverytracker.core.webservice.NetworkResult
import com.kvteam.deliverytracker.core.webservice.viewmodels.AccountResponse

interface ISession {
    fun getToken(): String?
    fun invalidateToken()
    fun setAccountExplicitly(
            username: String,
            password: String,
            token: String,
            user: User) : LoginResult
    fun hasAccount(): Boolean
    fun hasAccount(username: String?): Boolean

    suspend fun updateDeviceAsync()
    suspend fun checkSessionAsync(): CheckSessionResult
    suspend fun loginAsync(username: String, password: String): LoginResult
    suspend fun refreshUserInfoAsync(): NetworkResult<AccountResponse>
    suspend fun editUserInfoAsync(userInfo: User): NetworkResult<AccountResponse>
    suspend fun changePasswordAsync(old: CodePassword,
                                    new: CodePassword) : NetworkResult<AccountResponse>
    suspend fun logoutAsync()

    val user: User?
}