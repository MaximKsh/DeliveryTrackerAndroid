package com.kvteam.deliverytracker.core.session

import android.R.attr.accountType
import android.accounts.AbstractAccountAuthenticator
import android.accounts.Account
import android.accounts.AccountAuthenticatorResponse
import android.accounts.AccountManager
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import com.kvteam.deliverytracker.core.DeliveryTrackerApplication
import com.kvteam.deliverytracker.core.models.CodePassword
import com.kvteam.deliverytracker.core.webservice.IWebservice
import com.kvteam.deliverytracker.core.webservice.viewmodels.AccountRequest
import com.kvteam.deliverytracker.core.webservice.viewmodels.AccountResponse

class AccountAuthenticator(
        private val application: DeliveryTrackerApplication,
        private val webservice: IWebservice,
        private val sessionInfo: ISessionInfo): AbstractAccountAuthenticator(application) {
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
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun hasFeatures(p0: AccountAuthenticatorResponse?, p1: Account?, p2: Array<out String>?): Bundle {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getAuthToken(
            response: AccountAuthenticatorResponse?,
            account: Account?,
            authTokenType: String?,
            bundle: Bundle?): Bundle {
        response!!
        account!!
        authTokenType!!

        val am = AccountManager.get(application)

        // Берем токен из кэша
        var authToken = am.peekAuthToken(account, authTokenType)

        if (TextUtils.isEmpty(authToken)) {
            // В кэше токена нет, логинимся по новой
            val codePassword = CodePassword(account.name, am.getPassword(account))
            val accountResponse = webservice.post<AccountResponse>(
                    "/api/session/login",
                    AccountRequest(codePassword = codePassword),
                    AccountResponse::class.java)
            authToken = accountResponse.entity?.token
        }

        if (!TextUtils.isEmpty(authToken)) {
            // Есть токен, возвращаем бандл с токеном
            val result = Bundle()
            result.putString(AccountManager.KEY_ACCOUNT_NAME, account.name)
            result.putString(AccountManager.KEY_ACCOUNT_TYPE, account.type)
            result.putString(AccountManager.KEY_AUTHTOKEN, authToken)
            return result
        }

        // нет токена, редиректим на страницу логина
        val intent = Intent(application, (application).loginActivityType as Class<*>)
        intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response)
        intent.putExtra(sessionInfo.accountType, accountType)
        intent.putExtra(sessionInfo.authToken, authTokenType)

        val retBundle = Bundle()
        retBundle.putParcelable(AccountManager.KEY_INTENT, intent)
        return retBundle
    }

    override fun updateCredentials(p0: AccountAuthenticatorResponse?, p1: Account?, p2: String?, p3: Bundle?): Bundle {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun confirmCredentials(p0: AccountAuthenticatorResponse?, p1: Account?, p2: Bundle?): Bundle {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getAuthTokenLabel(p0: String?): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}