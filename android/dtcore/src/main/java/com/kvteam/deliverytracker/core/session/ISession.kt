package com.kvteam.deliverytracker.core.session

import com.kvteam.deliverytracker.core.models.UserModel

/**
 * Created by maxim on 18.10.17.
 */

interface ISession {
    fun getToken(): String?
    fun invalidateToken()

    fun login(username: String, password: String): LoginResult
    fun hasAccount(): Boolean
    fun hasAccount(username: String?): Boolean
    fun refreshUserInfo(): Boolean
    fun updateUserInfo(userInfo: UserModel): Boolean
    fun logout()

    val username: String?
    val surname: String?
    val name: String?
    val phoneNumber: String?
    val role: String?
}