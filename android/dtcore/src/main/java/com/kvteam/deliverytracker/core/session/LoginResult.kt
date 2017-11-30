package com.kvteam.deliverytracker.core.session

import com.kvteam.deliverytracker.core.common.SimpleResult
import java.util.*

open class LoginResult(
        val loginResultType: LoginResultType,
        fromNetwork : Boolean,
        errorChainId: UUID?) : SimpleResult(
            loginResultType in arrayOf(LoginResultType.Registered, LoginResultType.Success),
            fromNetwork,
            false,
            errorChainId)