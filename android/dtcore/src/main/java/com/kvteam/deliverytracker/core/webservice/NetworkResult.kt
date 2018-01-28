package com.kvteam.deliverytracker.core.webservice

import com.kvteam.deliverytracker.core.common.EntityResult
import com.kvteam.deliverytracker.core.models.IError
import com.kvteam.deliverytracker.core.webservice.viewmodels.ResponseBase

open class NetworkResult<out T : ResponseBase> (
        entity: T? = null,
        var fetched: Boolean = false,
        var statusCode: Int = 0,
        errors: List<IError> = listOf()) :
            EntityResult<T>(entity, true, false, errors)
