package com.kvteam.deliverytracker.core.session

import com.kvteam.deliverytracker.core.models.IError
import com.kvteam.deliverytracker.core.webservice.NetworkResult
import com.kvteam.deliverytracker.core.webservice.viewmodels.AccountResponse

open class LoginResult(
        val loginResultType: LoginResultType,
        entity: AccountResponse? = null,
        fetched : Boolean = false,
        statusCode: Int = 0,
        errors: List<IError> = listOf()) :
            NetworkResult<AccountResponse>(
                entity,
                fetched,
                statusCode,
                errors)