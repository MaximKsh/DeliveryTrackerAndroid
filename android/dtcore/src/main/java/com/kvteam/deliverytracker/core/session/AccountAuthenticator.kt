package com.kvteam.deliverytracker.core.session

import android.R.attr.accountType
import android.accounts.AbstractAccountAuthenticator
import android.accounts.Account
import android.accounts.AccountAuthenticatorResponse
import android.accounts.AccountManager
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import com.google.gson.JsonSyntaxException
import com.kvteam.deliverytracker.core.DeliveryTrackerApplication
import com.kvteam.deliverytracker.core.R
import com.kvteam.deliverytracker.core.common.EMPTY_STRING
import com.kvteam.deliverytracker.core.common.IDeliveryTrackerGsonProvider
import com.kvteam.deliverytracker.core.models.CodePassword
import com.kvteam.deliverytracker.core.webservice.CREATED_HTTP_STATUS
import com.kvteam.deliverytracker.core.webservice.IHttpManager
import com.kvteam.deliverytracker.core.webservice.OK_HTTP_STATUS
import com.kvteam.deliverytracker.core.webservice.viewmodels.AccountRequest
import com.kvteam.deliverytracker.core.webservice.viewmodels.AccountResponse

class AccountAuthenticator(
        gsonProvider: IDeliveryTrackerGsonProvider,
        private val application: DeliveryTrackerApplication,
        private val httpManager: IHttpManager,
        private val sessionInfo: ISessionInfo): AbstractAccountAuthenticator(application) {

    private val gson = gsonProvider.gson
    private val baseUrl: String = application.getString(R.string.Core_WebserviceUrl)

    override fun addAccount(
            response: AccountAuthenticatorResponse?,
            accountType: String?,
            authTokenType: String?,
            requiredFeatures: Array<out String>?,
            options: Bundle?): Bundle {
        val intent = Intent(application, (application).loginActivityType as Class<*>)

        intent.putExtra(sessionInfo.accountType, accountType)
        intent.putExtra(sessionInfo.authToken, authTokenType)
        intent.putExtra(SETTINGS_CONTEXT, true)
        intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response)

        val bundle = Bundle()
        bundle.putParcelable(AccountManager.KEY_INTENT, intent)

        return bundle
    }

    override fun editProperties(p0: AccountAuthenticatorResponse?, p1: String?): Bundle {
        TODO("not implemented")
    }

    override fun hasFeatures(p0: AccountAuthenticatorResponse?, p1: Account?, p2: Array<out String>?): Bundle {
        TODO("not implemented")
    }

    override fun getAuthToken(
            response: AccountAuthenticatorResponse,
            account: Account,
            authTokenType: String,
            bundle: Bundle): Bundle {

        val am = AccountManager.get(application)

        // Берем токен из кэша
        var authToken = am.peekAuthToken(account, authTokenType)

        if (TextUtils.isEmpty(authToken)) {
            // В кэше токена нет, логинимся по новой
            val codePassword = CodePassword(am.getUserData(account, CODE_KEY), am.getPassword(account))
            authToken = relogin(codePassword)
        }

        if (!TextUtils.isEmpty(authToken)) {
            // Есть токен, возвращаем бандл с токеном
            return putTokenIntoBundle(account, authToken)
        }

        // нет токена, редиректим на страницу логина
        return redirectToLogin(response, authTokenType)
    }

    override fun updateCredentials(p0: AccountAuthenticatorResponse?, p1: Account?, p2: String?, p3: Bundle?): Bundle {
        TODO("not implemented")
    }

    override fun confirmCredentials(p0: AccountAuthenticatorResponse?, p1: Account?, p2: Bundle?): Bundle {
        TODO("not implemented")
    }

    override fun getAuthTokenLabel(p0: String?): String {
        TODO("not implemented")
    }

    private fun relogin(codePassword: CodePassword) : String {
        val request = AccountRequest(codePassword = codePassword)
        val rawRequestBody = gson.toJson(request)
        val response = httpManager.post(
                baseUrl + "/api/account/login",
                rawRequestBody,
                mapOf(),
                "application/json")
        if(!response.success
                || response.statusCode !in OK_HTTP_STATUS..CREATED_HTTP_STATUS) {
            return EMPTY_STRING
        }

        val accountResponse = try {
            gson.fromJson<AccountResponse>(response.entity, AccountResponse::class.java)
        } catch (e: JsonSyntaxException) {
            return EMPTY_STRING
        }
        return accountResponse?.token ?: EMPTY_STRING
    }

    private fun putTokenIntoBundle(account: Account, authToken: String) : Bundle {
        val result = Bundle()
        result.putString(AccountManager.KEY_ACCOUNT_NAME, account.name)
        result.putString(AccountManager.KEY_ACCOUNT_TYPE, account.type)
        result.putString(AccountManager.KEY_AUTHTOKEN, authToken)
        return result
    }

    private fun redirectToLogin(response: AccountAuthenticatorResponse,
                                authTokenType: String) : Bundle{
        val intent = Intent(application, (application).loginActivityType as Class<*>)
        intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response)
        intent.putExtra(sessionInfo.accountType, accountType)
        intent.putExtra(sessionInfo.authToken, authTokenType)

        val retBundle = Bundle()
        retBundle.putParcelable(AccountManager.KEY_INTENT, intent)
        return retBundle
    }

}