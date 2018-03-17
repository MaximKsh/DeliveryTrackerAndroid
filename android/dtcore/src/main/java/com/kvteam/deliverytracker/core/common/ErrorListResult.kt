package com.kvteam.deliverytracker.core.common

import com.kvteam.deliverytracker.core.models.IError

open class ErrorListResult : BasicResult() {
    open var errors: List<IError> = listOf()

    override val success
        get() = errors.isEmpty()
}