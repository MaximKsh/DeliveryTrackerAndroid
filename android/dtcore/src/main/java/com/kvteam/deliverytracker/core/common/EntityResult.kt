package com.kvteam.deliverytracker.core.common

import com.kvteam.deliverytracker.core.models.IError

open class EntityResult<out T : Any?>(
        val entity : T?,
        fromNetwork : Boolean,
        fromCache : Boolean,
        errors: List<IError> = listOf()) : BasicResult(
            fromNetwork,
            fromCache,
            errors)