package com.kvteam.deliverytracker.core.session

import android.accounts.Account
import android.accounts.AccountManager
import android.content.Context
import android.os.Build
import com.google.gson.JsonSyntaxException
import com.kvteam.deliverytracker.core.R
import com.kvteam.deliverytracker.core.common.EMPTY_STRING
import com.kvteam.deliverytracker.core.common.buildDefaultGson
import com.kvteam.deliverytracker.core.models.CodePassword
import com.kvteam.deliverytracker.core.models.User
import com.kvteam.deliverytracker.core.roles.toRole
import com.kvteam.deliverytracker.core.webservice.*
import com.kvteam.deliverytracker.core.webservice.viewmodels.AccountRequest
import com.kvteam.deliverytracker.core.webservice.viewmodels.AccountResponse
import kotlinx.coroutines.experimental.async
import java.util.*
import java.util.concurrent.atomic.AtomicReference

class Session (
        private val httpManager: IHttpManager,
        private val sessionInfo: ISessionInfo,
        context: Context) : ISession {


    private val accountManager = AccountManager.get(context)
    private val gson = buildDefaultGson()
    private val baseUrl: String = context.getString(R.string.Core_WebserviceUrl)

    private val userAR = AtomicReference<Lazy<User?>>( lazy { getUserFromAccount() })

    override val user = userAR.get().value


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

            return result.getString(AccountManager.KEY_AUTHTOKEN)
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

    override fun setAccountExplicitly(
            username: String,
            password: String,
            token: String,
            user: User) : LoginResult {
        try{
            createAccount(username, password, token, user)
        } catch (e: Exception) {
            val result = LoginResult(LoginResultType.Error)
            result.fetched = false
            return result
        }
        return LoginResult(LoginResultType.Success)
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


    override suspend fun checkSessionAsync(): CheckSessionResult = async {
        val url = baseUrl + "/api/account/check"
        val result = doubleTimeRequest(this@Session, true) {
            httpManager.get(url, it)
        }
        // Если нет доступа в сеть, дальше нет смысла что-либо делать
        if(!result.fetched) {
            return@async CheckSessionResult.ServiceUnavailable
        }
        return@async when(result.statusCode) {
            OK_HTTP_STATUS -> CheckSessionResult.OK
            INVALID_TOKEN_HTTP_STATUS -> CheckSessionResult.Incorrect
            else -> CheckSessionResult.ServiceUnavailable
        }
    }.await()

    override suspend fun loginAsync(username: String, password: String): LoginResult = async {
        val request = AccountRequest(codePassword = CodePassword(username, password))
        val rawRequestBody = gson.toJson(request)
        val response = httpManager.post(
                baseUrl + "/api/account/login",
                rawRequestBody,
                mapOf(),
                "application/json")
        if(!response.success
            || response.statusCode !in OK_HTTP_STATUS..CREATED_HTTP_STATUS) {
            return@async LoginResult.error(response)
        }

        val accountResponse = try {
            gson.fromJson<AccountResponse>(response.entity, AccountResponse::class.java)
        } catch (e: JsonSyntaxException) {
            return@async LoginResult.error(response)
        }
        val tokenRole = accountResponse?.user?.role
        if(tokenRole != null
                && !sessionInfo.allowRoles.contains(tokenRole.toRole())) {
            return@async LoginResult.roleMismatch(response, accountResponse)
        }
        val token = accountResponse.token!!
        val user = accountResponse.user!!
        try{
            createAccount(username, password, token, user)
        } catch (e: Exception) {
            return@async LoginResult.error(response, accountResponse)
        }
        val resultType =
                if(response.statusCode == CREATED_HTTP_STATUS) LoginResultType.Registered
                else LoginResultType.Success
        return@async LoginResult.correct(resultType, response, accountResponse)
    }.await()

    override suspend fun refreshUserInfoAsync(): NetworkResult<AccountResponse> = async {
        val response = doubleTimeRequest(this@Session, true) {
            httpManager.get(
                    baseUrl + "/api/account/about",
                    it)
        }
        if(!response.success) {
            return@async NetworkResult.create<AccountResponse>(response)
        }

        val accountResponse = try {
            gson.fromJson<AccountResponse>(response.entity, AccountResponse::class.java)
        } catch (e: JsonSyntaxException) {
            return@async NetworkResult.create<AccountResponse>(response)
        }
        val userInfo = accountResponse.user
        setUserToAccount(userInfo)

        return@async NetworkResult.create(response,accountResponse)
    }.await()

    override suspend fun editUserInfoAsync(
            userInfo: User): NetworkResult<AccountResponse> = async {
        val request = AccountRequest(user = userInfo)
        val rawRequestBody = gson.toJson(request)
        val response = doubleTimeRequest(this@Session, true) {
            httpManager.post(
                    baseUrl + "/api/account/edit",
                    rawRequestBody,
                    it,
                    "application/json")
        }

        if(!response.success) {
            return@async NetworkResult.create<AccountResponse>(response)
        }

        val accountResponse = try {
            gson.fromJson<AccountResponse>(response.entity, AccountResponse::class.java)
        } catch (e: JsonSyntaxException) {
            return@async NetworkResult.create<AccountResponse>(response)
        }
        val newUserInfo = accountResponse.user
        setUserToAccount(newUserInfo)

        return@async NetworkResult.create(response,accountResponse)
    }.await()

    override suspend fun changePasswordAsync(
            old: CodePassword,
            new: CodePassword): NetworkResult<AccountResponse> = async {
        val request = AccountRequest(codePassword = old, newCodePassword = new)
        val rawRequestBody = gson.toJson(request)

        val response = doubleTimeRequest(this@Session, true) {
            httpManager.post(
                    baseUrl + "/api/account/change_password",
                    rawRequestBody,
                    it,
                    "application/json")
        }

        if(!response.success) {
            return@async NetworkResult.create<AccountResponse>(response)
        }

        val accountResponse = try {
            gson.fromJson<AccountResponse>(response.entity, AccountResponse::class.java)
        } catch (e: JsonSyntaxException) {
            return@async NetworkResult.create<AccountResponse>(response)
        }
        val account = accountManager.getAccountsByType(sessionInfo.accountType).firstOrNull()
        if(account != null) {
            accountManager.setPassword(account, new.password)
        }
        return@async NetworkResult.create(response,accountResponse)
    }.await()

    private fun getUserFromAccount(): User? {
        val user = User(
                code = getUserDataOrNull(CODE_KEY),
                surname = getUserDataOrNull(SURNAME_KEY),
                name = getUserDataOrNull(NAME_KEY),
                patronymic = getUserDataOrNull(PATRONYMIC_KEY),
                phoneNumber = getUserDataOrNull(PHONE_NUMBER_KEY),
                role = getUserDataUuidOrNull(ROLE_KEY))
        user.id = getUserDataUuidOrNull(ID_KEY)
        user.instanceId = getUserDataUuidOrNull(INSTANCE_ID_KEY)
        return user
    }

    private fun setUserToAccount(user: User?) {
        setUserDataOrNull(ID_KEY, user?.id)
        setUserDataOrNull(INSTANCE_ID_KEY, user?.instanceId)
        setUserDataOrNull(CODE_KEY, user?.code)
        setUserDataOrNull(SURNAME_KEY, user?.surname)
        setUserDataOrNull(NAME_KEY, user?.name)
        setUserDataOrNull(PATRONYMIC_KEY, user?.patronymic)
        setUserDataOrNull(ROLE_KEY, user?.role)
        userAR.set( lazy { getUserFromAccount() } )
    }


    private fun getUserDataOrNull(key: String): String? {
        val account = getFirstAccount() ?: return null
        return accountManager.getUserData(account, key)
    }

    private fun getUserDataUuidOrNull(key: String): UUID? {
        val idOrNull = getUserDataOrNull(key) ?: return null
        return UUID.fromString(idOrNull)
    }

    private fun setUserDataOrNull(key: String, value: Any?) {
        val account = getFirstAccount()
        if(account != null) {
            accountManager.setUserData(account, key, value?.toString() ?: EMPTY_STRING)
        }
    }

    private fun getFirstAccount() =
            accountManager.getAccountsByType(sessionInfo.accountType).firstOrNull()

    private fun createAccount(username: String, password: String, token: String, user: User) {
        val account = Account(formatAccountName(user), sessionInfo.accountType)
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
            val authTokenType = sessionInfo.accountType
            accountManager.addAccountExplicitly(account, password, null)
            accountManager.setAuthToken(account, authTokenType, token)
        } else {
            accountManager.setPassword(account, password)
        }

        setUserToAccount(user)
    }

    private fun formatAccountName(user: User) : String {
        if(user.name == null && user.surname == null) {
            return user.code ?: EMPTY_STRING
        }
        return "${user.name} ${user.surname}"
    }
}