package com.kvteam.deliverytracker.core.session

import android.accounts.Account
import android.accounts.AccountManager
import android.content.Context
import android.os.Build
import com.google.firebase.iid.FirebaseInstanceId
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.kvteam.deliverytracker.core.R
import com.kvteam.deliverytracker.core.common.ErrorItem
import com.kvteam.deliverytracker.core.common.IErrorManager
import com.kvteam.deliverytracker.core.common.ILocalizationManager
import com.kvteam.deliverytracker.core.common.SimpleResult
import com.kvteam.deliverytracker.core.models.CredentialsModel
import com.kvteam.deliverytracker.core.models.DeviceModel
import com.kvteam.deliverytracker.core.models.TokenModel
import com.kvteam.deliverytracker.core.models.UserModel
import com.kvteam.deliverytracker.core.webservice.IHttpManager
import com.kvteam.deliverytracker.core.webservice.NetworkResponse
import java.util.*

class Session (
        private val localizationManager: ILocalizationManager,
        private val errorManager: IErrorManager,
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
        val device = try {
            DeviceModel(FirebaseInstanceId.getInstance().token)
        } catch (e: IllegalStateException) {
            DeviceModel()
        }
        val credentials = CredentialsModel(username, password, device)
        val rawRequestBody = gson.toJson(credentials)
        val tokenResponse = httpManager.post(
                baseUrl + "/api/session/login",
                rawRequestBody,
                mapOf(),
                "application/json")
        val errorResult = handleLoginErrors(tokenResponse)
        if(errorResult != null) {
            return errorResult
        }

        val chainBuilder = errorManager.begin()
        chainBuilder.alias(localizationManager.getString(R.string.Core_Session_Error_Login))
        val token = try {
            gson.fromJson<TokenModel>(tokenResponse.responseEntity, TokenModel::class.java)
        } catch (e: JsonSyntaxException) {
            chainBuilder.add(ErrorItem(localizationManager.getString(R.string.Core_Error_InvalidJson)))
            return LoginResult(
                    LoginResultType.Error,
                    true,
                    chainBuilder.complete())
        }
        val tokenRole = token.user.role
        if(tokenRole != null
                && !sessionInfo.allowRoles.contains(tokenRole)) {
            chainBuilder.add(ErrorItem(localizationManager.getString(R.string.Core_Error_InvalidRole)))
            return LoginResult(
                    LoginResultType.RoleMismatch,
                    true,
                    chainBuilder.complete())
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
            chainBuilder.add(ErrorItem(localizationManager.getString(R.string.Core_UnknownError)))
            return LoginResult(
                    LoginResultType.Error,
                    true,
                    chainBuilder.complete())
        }

        this.username = username
        surname = token.user.surname ?: "no surname"
        name = token.user.name ?: "no name"
        phoneNumber = token.user.phoneNumber ?: "no phone number"
        role = tokenRole ?: "no role"

        if(tokenResponse.statusCode == 201) {
            return LoginResult(LoginResultType.Registered, true, null)
        }
        return LoginResult(LoginResultType.Success, true, null)
    }

    override fun refreshUserInfo(): SimpleResult {
        val response = httpManager.get(
                baseUrl + "/api/session/check",
                mapOf())
        val errorResult = handleUserInfoErrors(
                response,
                localizationManager.getString(R.string.Core_Session_Error_RefreshUserInfo))
        if(errorResult != null) {
            return errorResult
        }

        val userInfo = try {
            gson.fromJson<UserModel>(response.responseEntity, UserModel::class.java)
        } catch (e: JsonSyntaxException) {
            val chainId = errorManager.begin()
                    .alias(localizationManager.getString(R.string.Core_Session_Error_RefreshUserInfo))
                    .add(ErrorItem(localizationManager.getString(R.string.Core_Error_InvalidJson)))
                    .complete()
            return SimpleResult(false, false, false, chainId)
        }

        surname = userInfo.surname ?: "no surname"
        name = userInfo.name ?: "no name"
        phoneNumber = userInfo.phoneNumber ?: "no phone number"
        role = userInfo.role ?: "no role"

        return SimpleResult(true, true, false, null)
    }

    override fun editUserInfo(userInfo: UserModel): SimpleResult {
        val rawRequestBody = gson.toJson(userInfo)
        var headers = getAuthorizationHeaders(this)
                ?: return nullHeaderSimpleUserInfo(R.string.Core_Session_Error_EditUserInfo)
        var response = httpManager.post(
                baseUrl + "/api/user/edit",
                rawRequestBody,
                headers,
                "application/json")
        if(response.statusCode == 401) {
            invalidateToken()
            headers = getAuthorizationHeaders(this)
                    ?: return nullHeaderSimpleUserInfo(R.string.Core_Session_Error_EditUserInfo)
            response = httpManager.post(
                    baseUrl + "/api/user/edit",
                    rawRequestBody,
                    headers,
                    "application/json")
        }
        val errorResult = handleUserInfoErrors(
                response,
                localizationManager.getString(R.string.Core_Session_Error_EditUserInfo))
        if(errorResult != null) {
            return errorResult
        }

        val newUserInfo = try {
            gson.fromJson<UserModel>(response.responseEntity, UserModel::class.java)
        } catch (e: JsonSyntaxException) {
            val chainId = errorManager.begin()
                    .alias(localizationManager.getString(R.string.Core_Session_Error_EditUserInfo))
                    .add(ErrorItem(localizationManager.getString(R.string.Core_Error_InvalidJson)))
                    .complete()
            return SimpleResult(false, true, false, chainId)
        }

        surname = newUserInfo.surname ?: "no surname"
        name = newUserInfo.name ?: "no name"
        phoneNumber = newUserInfo.phoneNumber ?: "no phone number"
        role = newUserInfo.role ?: "no role"

        return SimpleResult(true, true, false, null)
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

    private fun handleLoginErrors(
            tokenResponse: NetworkResponse<String>) : LoginResult? {
        val chainBuilder = errorManager.begin()
        chainBuilder.alias(localizationManager.getString(R.string.Core_Session_Error_Login))
        if(!tokenResponse.fetched) {
            chainBuilder.add(ErrorItem(localizationManager.getString(R.string.Core_Error_NetworkError)))
            return LoginResult(
                    LoginResultType.Error,
                    false,
                    chainBuilder.complete())
        }
        if(tokenResponse.statusCode == 401) {
            chainBuilder.add(ErrorItem(localizationManager.getString(R.string.Core_Session_Error_WrongCredentials)))
            return LoginResult(
                    LoginResultType.Error,
                    true,
                    chainBuilder.complete())
        }
        if(tokenResponse.statusCode !in 200..299
                || tokenResponse.responseEntity == null) {
            val items = tokenResponse.errorList?.errors
            if (items != null) {
                for(it in items) {
                    chainBuilder.add(ErrorItem(localizationManager.getString(it.message)))
                }
            }
            return LoginResult(
                    LoginResultType.Error,
                    true,
                    chainBuilder.complete())
        }
        return null
    }

    private fun handleUserInfoErrors(
            response: NetworkResponse<String>,
            alias: String) : SimpleResult? {
        val chainBuilder = errorManager.begin()
        chainBuilder.alias(alias)
        if(!response.fetched) {
            chainBuilder.add(ErrorItem(localizationManager.getString(R.string.Core_Error_NetworkError)))
            return SimpleResult(
                    false,
                    false,
                    false,
                    chainBuilder.complete())
        }
        if(response.statusCode !in 200..299
                || response.responseEntity == null) {
            val items = response.errorList?.errors
            if (items != null) {
                for(it in items) {
                    chainBuilder.add(ErrorItem(localizationManager.getString(it.message)))
                }
            }
            return LoginResult(
                    LoginResultType.Error,
                    true,
                    chainBuilder.complete())
        }
        return null
    }

    private fun nullHeaderSimpleUserInfo(resId: Int) =
            SimpleResult(
                    false,
                    false,
                    false,
                    getUnauthorizedErrorChain(localizationManager.getString(resId)))

    private fun getUnauthorizedErrorChain(alias: String): UUID {
        return errorManager
                .begin()
                .alias(alias)
                .add(ErrorItem(localizationManager.getString(R.string.Core_Session_Error_Unauthorized)))
                .complete()
    }

}