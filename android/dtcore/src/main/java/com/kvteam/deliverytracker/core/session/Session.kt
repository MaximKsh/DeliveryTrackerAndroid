package com.kvteam.deliverytracker.core.session

import android.accounts.Account
import android.accounts.AccountManager
import android.content.Context
import android.os.Build
import com.google.gson.JsonSyntaxException
import com.kvteam.deliverytracker.core.R
import com.kvteam.deliverytracker.core.common.EMPTY_STRING
import com.kvteam.deliverytracker.core.common.buildDefaultGson
import com.kvteam.deliverytracker.core.common.invalidResponseBody
import com.kvteam.deliverytracker.core.common.unauthorized
import com.kvteam.deliverytracker.core.models.CodePassword
import com.kvteam.deliverytracker.core.models.User
import com.kvteam.deliverytracker.core.roles.toRole
import com.kvteam.deliverytracker.core.webservice.IHttpManager
import com.kvteam.deliverytracker.core.webservice.NetworkResult
import com.kvteam.deliverytracker.core.webservice.viewmodels.AccountRequest
import com.kvteam.deliverytracker.core.webservice.viewmodels.AccountResponse
import java.util.*

class Session (
        private val httpManager: IHttpManager,
        private val sessionInfo: ISessionInfo,
        context: Context) : ISession {
    private val gson = buildDefaultGson()
    private var baseUrl: String = context.getString(R.string.Core_WebserviceUrl)

    override var id: UUID?
        get() {
            val idOrNull = getUserDataOrNull("_id")
            return if (idOrNull != null) {
                UUID.fromString(idOrNull)
            } else {
                null
            }
        }
        private set(v) = setUserDataOrNull("_id", v?.toString())

    override var instanceId: UUID?
        get() {
            val idOrNull = getUserDataOrNull("_instanceId")
            return if (idOrNull != null) {
                UUID.fromString(idOrNull)
            } else {
                null
            }
        }
        private set(v) = setUserDataOrNull("_instanceId", v?.toString())

    override var code: String?
        get() = getUserDataOrNull("_username")
        private set(v) = setUserDataOrNull("_username", v)
    override var surname: String?
        get() = getUserDataOrNull("_surname")
        private set(v) = setUserDataOrNull("_surname", v)
    override var name: String?
        get() = getUserDataOrNull("_name")
        private set(v) = setUserDataOrNull("_name", v)
    override var patronymic: String?
        get() = getUserDataOrNull("_patronymic")
        private set(v) = setUserDataOrNull("_patronymic", v)
    override var phoneNumber: String?
        get() = getUserDataOrNull("_phoneNumber")
        private set(v) = setUserDataOrNull("_phoneNumber", v)
    override var role: UUID?
        get() {
            val roleOrNull = getUserDataOrNull("_role")
            return if (roleOrNull != null) {
                UUID.fromString(roleOrNull)
            } else {
                null
            }
        }
        private set(v) = setUserDataOrNull("_role", v?.toString())

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

    override fun checkSession(): CheckSessionResult {
        val url = baseUrl + "/api/account/check"
        var headers = getAuthorizationHeaders(this) ?: return CheckSessionResult.Wrong
        var result = httpManager.get(url, headers)
        // Если нет доступа в сеть, дальше нет смысла что-либо делать
        if(!result.fetched) {
            return CheckSessionResult.Undefined
        }

        if (result.statusCode == 403) {
            invalidateToken()
            headers = getAuthorizationHeaders(this) ?: return CheckSessionResult.Wrong
            result = httpManager.get(url, headers)
        }
        if(!result.fetched) {
            return CheckSessionResult.Undefined
        }
        return when(result.statusCode) {
            200 -> CheckSessionResult.Correct
            403 -> CheckSessionResult.Wrong
            else -> CheckSessionResult.Undefined
        }
    }

    override fun setTokenExplicitly(token: String) : Boolean {
        val account = accountManager.getAccountsByType(sessionInfo.accountType).firstOrNull()
        if(account != null) {
            val authtokenType = sessionInfo.accountType
            accountManager.setAuthToken(account, authtokenType, token)
            return true
        }
        return false
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

    override fun login(username: String, password: String): LoginResult {
        val request = AccountRequest(codePassword = CodePassword(username, password))
        val rawRequestBody = gson.toJson(request)
        val response = httpManager.post(
                baseUrl + "/api/account/login",
                rawRequestBody,
                mapOf(),
                "application/json")
        if(!response.success
            || response.statusCode !in 200..201) {
            return LoginResult(
                    LoginResultType.Error,
                    fetched = response.fetched,
                    statusCode = response.statusCode,
                    errors = response.errors)
        }

        val accountResponse = try {
            gson.fromJson<AccountResponse>(response.entity, AccountResponse::class.java)
        } catch (e: JsonSyntaxException) {
            return LoginResult(
                    LoginResultType.Error,
                    fetched = true,
                    statusCode = response.statusCode,
                    errors = response.errors)
        }
        val tokenRole = accountResponse?.user?.role
        if(tokenRole != null
                && !sessionInfo.allowRoles.contains(tokenRole.toRole())) {
            return LoginResult(
                    LoginResultType.RoleMismatch,
                    fetched = true,
                    statusCode = response.statusCode,
                    errors = response.errors)
        }
        val token = accountResponse.token

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
                accountManager.setAuthToken(account, authtokenType, token)
            } else {
                accountManager.setPassword(account, password)
            }
        } catch (e: Exception) {
            return LoginResult(
                    LoginResultType.Error,
                    fetched = true,
                    statusCode = response.statusCode,
                    errors = response.errors)
        }

        val user = accountResponse.user
        id = user?.id
        instanceId = user?.instanceId
        code = username
        surname = user?.surname ?: EMPTY_STRING
        name = user?.name ?: EMPTY_STRING
        patronymic = user?.patronymic ?: EMPTY_STRING
        phoneNumber = user?.phoneNumber ?: EMPTY_STRING
        role = tokenRole

        val resultType =
                if(response.statusCode == 201) LoginResultType.Registered
                else LoginResultType.Success
        return LoginResult(
                resultType,
                accountResponse,
                fetched = true,
                statusCode = response.statusCode)
    }

    override fun refreshUserInfo(): NetworkResult<AccountResponse> {
        var headers = getAuthorizationHeaders(this)
                ?: return NetworkResult(errors = listOf(unauthorized()))
        var response = httpManager.get(
                baseUrl + "/api/account/about",
                headers)
        if(response.statusCode == 401) {
            invalidateToken()
            headers = getAuthorizationHeaders(this)
                    ?: return NetworkResult(errors = listOf(unauthorized()))
            response = httpManager.get(
                    baseUrl + "/api/account/about",
                    headers)
        }
        if(!response.success) {
            return NetworkResult(
                    fetched = response.fetched,
                    statusCode = response.statusCode,
                    errors = response.errors)
        }

        val accountResponse = try {
            gson.fromJson<AccountResponse>(response.entity, AccountResponse::class.java)
        } catch (e: JsonSyntaxException) {
            return NetworkResult(
                    fetched = response.fetched,
                    statusCode = response.statusCode,
                    errors = listOf(invalidResponseBody()))
        }
        val userInfo = accountResponse.user
        if( userInfo != null) {
            surname = userInfo.surname ?: "no surname"
            name = userInfo.name ?: "no name"
            phoneNumber = userInfo.phoneNumber ?: "no phone number"
            role = userInfo.role
        }

        return NetworkResult(
                entity = accountResponse,
                statusCode = response.statusCode,
                fetched = response.fetched,
                errors = response.errors)
    }

    override fun editUserInfo(userInfo: User): NetworkResult<AccountResponse> {
        val request = AccountRequest(user = userInfo)
        val rawRequestBody = gson.toJson(request)
        var headers = getAuthorizationHeaders(this)
                ?: return NetworkResult(errors = listOf(unauthorized()))
        var response = httpManager.post(
                baseUrl + "/api/account/edit",
                rawRequestBody,
                headers,
                "application/json")
        if(response.statusCode == 401) {
            invalidateToken()
            headers = getAuthorizationHeaders(this)
                    ?: return NetworkResult(errors = listOf(unauthorized()))
            response = httpManager.post(
                    baseUrl + "/api/account/edit",
                    rawRequestBody,
                    headers,
                    "application/json")
        }
        if(!response.success) {
            return NetworkResult(
                    fetched = response.fetched,
                    statusCode = response.statusCode,
                    errors = response.errors)
        }

        val accountResponse = try {
            gson.fromJson<AccountResponse>(response.entity, AccountResponse::class.java)
        } catch (e: JsonSyntaxException) {
            return NetworkResult(
                    fetched = response.fetched,
                    statusCode = response.statusCode,
                    errors = listOf(invalidResponseBody()))
        }
        val newUserInfo = accountResponse.user
        if( newUserInfo != null) {
            surname = newUserInfo.surname ?: EMPTY_STRING
            name = newUserInfo.name ?: EMPTY_STRING
            patronymic = newUserInfo.patronymic ?: EMPTY_STRING
            phoneNumber = newUserInfo.phoneNumber ?: "no phone number"
            role = newUserInfo.role
        }

        return NetworkResult(
                entity = accountResponse,
                statusCode = response.statusCode,
                fetched = response.fetched,
                errors = response.errors)
    }

    override fun changePassword(old: CodePassword, new: CodePassword): NetworkResult<AccountResponse> {
        val request = AccountRequest(codePassword = old, newCodePassword = new)
        val rawRequestBody = gson.toJson(request)
        var headers = getAuthorizationHeaders(this)
                ?: return NetworkResult(errors = listOf(unauthorized()))
        var response = httpManager.post(
                baseUrl + "/api/account/change_password",
                rawRequestBody,
                headers,
                "application/json")
        if(response.statusCode == 401) {
            invalidateToken()
            headers = getAuthorizationHeaders(this)
                    ?: return NetworkResult(errors = listOf(unauthorized()))
            response = httpManager.post(
                    baseUrl + "/api/account/change_password",
                    rawRequestBody,
                    headers,
                    "application/json")
        }
        if(!response.success) {
            return NetworkResult(
                    fetched = response.fetched,
                    statusCode = response.statusCode,
                    errors = response.errors)
        }

        val accountResponse = try {
            gson.fromJson<AccountResponse>(response.entity, AccountResponse::class.java)
        } catch (e: JsonSyntaxException) {
            return NetworkResult(
                    fetched = response.fetched,
                    statusCode = response.statusCode,
                    errors = listOf(invalidResponseBody()))
        }
        val account = accountManager.getAccountsByType(sessionInfo.accountType).firstOrNull()
        if(account != null) {
            accountManager.setPassword(account, new.password)
        }
        return NetworkResult(
                entity = accountResponse,
                statusCode = response.statusCode,
                fetched = response.fetched,
                errors = response.errors)
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