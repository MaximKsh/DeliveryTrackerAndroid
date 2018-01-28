package com.kvteam.deliverytracker.core.common

import com.kvteam.deliverytracker.core.models.IError


open class BasicResult(
        val fromNetwork : Boolean,
        val fromCache : Boolean,
        val errors: List<IError>) {

    val success = errors.isEmpty()

}