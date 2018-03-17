package com.kvteam.deliverytracker.core.session

import com.kvteam.deliverytracker.core.webservice.NetworkResult
import com.kvteam.deliverytracker.core.webservice.RawNetworkResult
import com.kvteam.deliverytracker.core.webservice.viewmodels.AccountResponse

class LoginResult(
        val loginResultType: LoginResultType
) :  NetworkResult<AccountResponse>() {
    companion object {
        fun error(response: RawNetworkResult,
                  accountResponse: AccountResponse? = null) : LoginResult {
            val result = LoginResult(LoginResultType.Error)
            result.fetched = response.fetched
            result.statusCode = response.statusCode
            if(accountResponse != null) {
                result.entity = accountResponse
            }
            return result
        }

        fun roleMismatch(response: RawNetworkResult,
                         accountResponse: AccountResponse? = null) : LoginResult {
            val result = LoginResult(LoginResultType.RoleMismatch)
            result.fetched = response.fetched
            result.statusCode = response.statusCode
            if(accountResponse != null) {
                result.entity = accountResponse
            }
            return result
        }

        fun correct(resultType: LoginResultType,
                    response: RawNetworkResult,
                    accountResponse: AccountResponse) : LoginResult {
            val result = LoginResult(resultType)
            result.fetched = response.fetched
            result.statusCode = response.statusCode
            result.entity = accountResponse
            return result
        }
    }

}