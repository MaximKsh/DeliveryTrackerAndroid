package com.kvteam.deliverytracker.core.session

import android.accounts.Account
import android.accounts.AccountManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import com.google.firebase.iid.FirebaseInstanceId
import com.google.gson.JsonSyntaxException
import com.kvteam.deliverytracker.core.common.Configuration
import com.kvteam.deliverytracker.core.common.EMPTY_STRING
import com.kvteam.deliverytracker.core.common.IDeliveryTrackerGsonProvider
import com.kvteam.deliverytracker.core.common.webserviceURL
import com.kvteam.deliverytracker.core.models.CodePassword
import com.kvteam.deliverytracker.core.models.Device
import com.kvteam.deliverytracker.core.models.Instance
import com.kvteam.deliverytracker.core.models.User
import com.kvteam.deliverytracker.core.roles.toRole
import com.kvteam.deliverytracker.core.webservice.*
import com.kvteam.deliverytracker.core.webservice.viewmodels.AccountRequest
import com.kvteam.deliverytracker.core.webservice.viewmodels.AccountResponse
import com.kvteam.deliverytracker.core.webservice.viewmodels.InstanceResponse
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.runBlocking
import java.util.*
import java.util.concurrent.atomic.AtomicReference


class Session (
        gsonProvider: IDeliveryTrackerGsonProvider,
        configuration: Configuration,
        private val httpManager: IHttpManager,
        private val sessionInfo: ISessionInfo,
        private val context: Context) : ISession {

    private val deviceType = "ANDROID"

    val gson = gsonProvider.gson

    private val accountManager = AccountManager.get(context)
    private val baseUrl: String = configuration.webserviceURL

    private val userAR = AtomicReference<Lazy<User?>>( lazy { getUserFromAccount() })
    private val instanceAR = AtomicReference<Lazy<Instance?>> (lazy { getInstanceFromAccount() })

    override val user
        get() = userAR.get().value
    override val instance
        get() = instanceAR.get().value


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
            runBlocking {
                async {
                    tryRefreshInstanceInfo()
                }.await()
            }
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

    override suspend fun updateDeviceAsync(): Unit = async {
        val refreshedToken = FirebaseInstanceId.getInstance().token ?: return@async
        val user = this@Session.user ?: return@async

        val device = Device()
        device.userId = user.id
        device.type = deviceType
        device.version = Build.VERSION.SDK_INT.toString()
        device.language = Locale.getDefault().language
        device.firebaseId = refreshedToken
        try {
            val pInfo = this@Session.context.packageManager.getPackageInfo(this@Session.context.packageName, 0)
            device.applicationType = pInfo.packageName
            device.applicationVersion = pInfo.versionCode.toString()
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }


        val request = AccountRequest(device = device)
        val rawRequestBody = gson.toJson(request)
        doubleTimeRequest(this@Session, true) {
            httpManager.post(
                    baseUrl + "/api/account/update_device",
                    rawRequestBody,
                    it,
                    "application/json")
        }
    }.await()

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

        tryRefreshInstanceInfo()
        updateDeviceAsync()

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
        tryRefreshInstanceInfo()

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
        tryRefreshInstanceInfo()

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

    override suspend fun logoutAsync() = async {
        val headers =  getAuthorizationHeaders(this@Session)
        if (headers != null) {
            httpManager.post(
                    baseUrl + "/api/account/logout",
                    EMPTY_STRING,
                    headers,
                    "application/json")
        }

        accountManager.getAccountsByType(sessionInfo.accountType)
                .forEach {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                        accountManager.removeAccountExplicitly(it)
                    } else {
                        @Suppress("DEPRECATION")
                        accountManager.removeAccount(it, null, null)
                    }
                }

    }.await()

    private suspend fun tryRefreshInstanceInfo() = async {
        val response = doubleTimeRequest(this@Session, true) {
            httpManager.get(
                    baseUrl + "/api/instance/get",
                    it)
        }
        if(!response.success) {
            return@async
        }

        val accountResponse = try {
            gson.fromJson<InstanceResponse>(response.entity, InstanceResponse::class.java)
        } catch (e: JsonSyntaxException) {
            return@async
        }
        val info = accountResponse.instance
        setInstanceToAccount(info)
    }.await()

    private fun getInstanceFromAccount(): Instance? {
        val instance = Instance(
                id = getDataUuidOrNull(INSTANCE_ID_KEY),
                name = getDataOrNull(INSTANCE_NAME_KEY),
                creatorId = getDataUuidOrNull(INSTANCE_CREATOR_ID_KEY)
        )
        return instance
    }

    private fun getUserFromAccount(): User? {
        val user = User(
                code = getDataOrNull(CODE_KEY),
                surname = getDataOrNull(SURNAME_KEY),
                name = getDataOrNull(NAME_KEY),
                patronymic = getDataOrNull(PATRONYMIC_KEY),
                phoneNumber = getDataOrNull(PHONE_NUMBER_KEY),
                role = getDataUuidOrNull(ROLE_KEY))
        user.id = getDataUuidOrNull(ID_KEY)
        user.instanceId = getDataUuidOrNull(INSTANCE_ID_KEY)
        return user
    }

    private fun setUserToAccount(user: User?) {
        setDataOrNull(ID_KEY, user?.id)
        setDataOrNull(INSTANCE_ID_KEY, user?.instanceId)
        setDataOrNull(CODE_KEY, user?.code)
        setDataOrNull(SURNAME_KEY, user?.surname)
        setDataOrNull(NAME_KEY, user?.name)
        setDataOrNull(PATRONYMIC_KEY, user?.patronymic)
        setDataOrNull(PHONE_NUMBER_KEY, user?.phoneNumber)
        setDataOrNull(ROLE_KEY, user?.role)
        userAR.set( lazy { getUserFromAccount() } )
    }

    private fun setInstanceToAccount(instance: Instance?) {
        setDataOrNull(INSTANCE_ID_KEY, instance?.id)
        setDataOrNull(INSTANCE_NAME_KEY, instance?.name)
        setDataOrNull(INSTANCE_CREATOR_ID_KEY, instance?.creatorId)
        instanceAR.set( lazy { getInstanceFromAccount() } )
    }

    private fun getDataOrNull(key: String): String? {
        val account = getFirstAccount() ?: return null
        return accountManager.getUserData(account, key)
    }

    private fun getDataUuidOrNull(key: String): UUID? {
        val idOrNull = getDataOrNull(key) ?: return null
        return UUID.fromString(idOrNull)
    }

    private fun setDataOrNull(key: String, value: Any?) {
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