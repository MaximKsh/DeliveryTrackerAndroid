package com.kvteam.deliverytracker.core.webservice.viewmodels

import com.kvteam.deliverytracker.core.models.CodePassword
import com.kvteam.deliverytracker.core.models.User

data class AccountRequest(
        var user: User? = null,
        var codePassword: CodePassword? = null,
        var newCodePassword: CodePassword? = null
): RequestBase()