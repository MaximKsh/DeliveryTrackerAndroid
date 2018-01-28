package com.kvteam.deliverytracker.core.webservice

import com.kvteam.deliverytracker.core.common.EntityResult
import com.kvteam.deliverytracker.core.models.IError

class RawNetworkResult(
        bodyContent: String? = null,
        var fetched: Boolean = false,
        var statusCode: Int = 0,
        errors: List<IError> = listOf()) :
        EntityResult<String>(bodyContent, true, false, errors)
