package com.kvteam.deliverytracker.core.session

import android.accounts.Account
import android.accounts.AccountManager
import android.content.Context
import android.os.Build
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.kvteam.deliverytracker.core.R
import com.kvteam.deliverytracker.core.models.CredentialsModel
import com.kvteam.deliverytracker.core.models.TokenModel
import com.kvteam.deliverytracker.core.models.UserModel
import com.kvteam.deliverytracker.core.webservice.IHttpManager

class Session (
        private val httpManager: IHttpManager,
        private val sessionInfo: ISessionInfo,
        context: Context) : ISession {
    private val gson = Gson()
    private var baseUrl: String = context.getString(R.string.Core_WebserviceUrl)

    override var username: String?
        get() = getUserDataOrNull("_username")
        private set(v) = setUserDataOrNull("_username", v)
    override var surname: String?
        get() = getUserDataOrNull("_surname")
        private set(v) = setUserDataOrNull("_surname", v)
    override var name: String?
        get() = getUserDataOrNull("_name")
        private set(v) = setUserDataOrNull("_name", v)
    override var phoneNumber: String?
        get() = getUserDataOrNull("_phoneNumber")
        private set(v) = setUserDataOrNull("_phoneNumber", v)
    override var role: String?
        get() = getUserDataOrNull("_role")
        private set(v) = setUserDataOrNull("_role", v)

    private val accountManager = AccountManager.get(context)

    override fun getToken(): String? {
        try {
            val account = getFirstAccount()

            val result = accountManager.getAuthToken(
                    account,
                    sessionInfo.accountType,
                    null,
                    false,
                    null,
                    null)
                    .result

            return result.getString("authtoken")
        } catch(e: Exception) {
            return null
        }
    }

    override fun invalidateToken() {
        val account = getFirstAccount()
        val token = accountManager.peekAuthToken(account, sessionInfo.accountType)
        if(token != null) {
            accountManager.invalidateAuthToken(sessionInfo.accountType, token)
        }
    }

    override fun login(username: String, password: String): LoginResult {
        val credentials = CredentialsModel(username, password)
        val rawRequestBody = gson.toJson(credentials)
        val tokenResponse = httpManager.post(
                baseUrl + "/api/session/login",
                rawRequestBody,
                mapOf(),
                "application/json")
        if(!tokenResponse.fetched
                || tokenResponse.statusCode !in 200..299
                || tokenResponse.responseEntity == null) {
            return LoginResult.Error
        }

        val token = try {
            gson.fromJson<TokenModel>(tokenResponse.responseEntity, TokenModel::class.java)
        } catch (e: JsonSyntaxException) {
            return LoginResult.Error
        }
        val tokenRole = token.user.role
        if(tokenRole != null
                && !sessionInfo.allowRoles.contains(tokenRole)) {
            return LoginResult.RoleMismatch
        }

        val account = Account(username, sessionInfo.accountType)
        try{
            // Сначала удаляются все предыдущие
            accountManager.getAccountsByType(sessionInfo.accountType)
                    .filter { it != account }
                    .forEach {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                            accountManager.removeAccountExplicitly(it)
                        } else {
                            @Suppress("DEPRECATION")
                            accountManager.removeAccount(it, null, null)
                        }
                    }

            if (!hasAccount(username)) {
                val authtokenType = sessionInfo.accountType
                accountManager.addAccountExplicitly(account, password, null)
                accountManager.setAuthToken(account, authtokenType, token.token)
            } else {
                accountManager.setPassword(account, password)
            }
        } catch (e: Exception) {
            return LoginResult.Error
        }

        this.username = username
        surname = token.user.surname ?: "no surname"
        name = token.user.name ?: "no name"
        phoneNumber = token.user.phoneNumber ?: "no phone number"
        role = tokenRole ?: "no role"

        return if(tokenResponse.statusCode == 201) LoginResult.Registered  else LoginResult.Success
    }

    override fun refreshUserInfo(): Boolean {
        val response = httpManager.get(
                baseUrl + "/api/session/check",
                mapOf())
        if(!response.fetched
                || response.statusCode !in 200..299
                || response.responseEntity == null) {
            return false
        }

        val userInfo = try {
            gson.fromJson<UserModel>(response.responseEntity, UserModel::class.java)
        } catch (e: JsonSyntaxException) {
            return false
        }

        surname = userInfo.surname ?: "no surname"
        name = userInfo.name ?: "no name"
        phoneNumber = userInfo.phoneNumber ?: "no phone number"
        role = userInfo.role ?: "no role"

        return true
    }

    override fun updateUserInfo(userInfo: UserModel): Boolean {
        val rawRequestBody = gson.toJson(userInfo)
        var headers = getAuthorizationHeaders(this) ?: return false
        var response = httpManager.post(
                baseUrl + "/api/user/modify",
                rawRequestBody,
                headers,
                "application/json")
        if(response.statusCode == 401) {
            invalidateToken()
            headers = getAuthorizationHeaders(this) ?: return false
            response = httpManager.get(baseUrl + "/api/user/modify", headers)
        }

        if(!response.fetched
                || response.statusCode !in 200..299
                || response.responseEntity == null) {
            return false
        }

        val newUserInfo = try {
            gson.fromJson<UserModel>(response.responseEntity, UserModel::class.java)
        } catch (e: JsonSyntaxException) {
            return false
        }

        surname = newUserInfo.surname ?: "no surname"
        name = newUserInfo.name ?: "no name"
        phoneNumber = newUserInfo.phoneNumber ?: "no phone number"
        role = newUserInfo.role ?: "no role"

        return true
    }

    override fun hasAccount(): Boolean {
        return accountManager.getAccountsByType(sessionInfo.accountType).isNotEmpty()
    }

    override fun hasAccount(username: String?): Boolean {
        if(username == null) {
            return false
        }
        val accounts = accountManager.getAccountsByType(sessionInfo.accountType)
        return accounts.any { p -> p.name == username }
    }

    override fun logout() {
        accountManager.getAccountsByType(sessionInfo.accountType)
                .forEach {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                        accountManager.removeAccountExplicitly(it)
                    } else {
                        @Suppress("DEPRECATION")
                        accountManager.removeAccount(it, null, null)
                    }
                }
    }

    private fun getUserDataOrNull(key: String): String? {
        val account = getFirstAccount()
        return if(account != null) accountManager.getUserData(account, key)
               else null
    }

    private fun setUserDataOrNull(key: String, value: String?) {
        val account = getFirstAccount()
        if(account != null) {
            accountManager.setUserData(account, key, value)
        }

    }

    private fun getFirstAccount() =
            accountManager.getAccountsByType(sessionInfo.accountType).firstOrNull()
}