package com.kvteam.deliverytracker.core.session

import com.kvteam.deliverytracker.core.common.SimpleResult
import com.kvteam.deliverytracker.core.models.UserModel

interface ISession {
    fun getToken(): String?
    fun invalidateToken()

    fun login(username: String, password: String): LoginResult
    fun hasAccount(): Boolean
    fun hasAccount(username: String?): Boolean
    fun refreshUserInfo(): SimpleResult
    fun editUserInfo(userInfo: UserModel): SimpleResult
    fun logout()

    val username: String?
    val surname: String?
    val name: String?
    val phoneNumber: String?
    val role: String?
}