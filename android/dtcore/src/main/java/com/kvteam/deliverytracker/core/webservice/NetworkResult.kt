package com.kvteam.deliverytracker.core.webservice

import com.kvteam.deliverytracker.core.common.EMPTY_ERROR_ARRAY
import com.kvteam.deliverytracker.core.common.EntityResult
import com.kvteam.deliverytracker.core.models.IError
import com.kvteam.deliverytracker.core.webservice.viewmodels.ResponseBase

open class NetworkResult<T : ResponseBase> (
        open var fetched: Boolean = false,
        open var noToken: Boolean = false,
        open var statusCode: Int = 0
) : EntityResult<T>() {
    override var errors: List<IError>
        get() = entity?.errors ?: EMPTY_ERROR_ARRAY
        set(value) = throw NotImplementedError()

    override val success: Boolean
        get() = errors.isEmpty() && fetched && statusCode in OK_HTTP_STATUS..CREATED_HTTP_STATUS

    companion object {
        fun <T : ResponseBase> create(
                rawResult: RawNetworkResult,
                response: T? = null) : NetworkResult<T> {
            val result = NetworkResult<T>()
            result.fetched = rawResult.fetched
            result.noToken = rawResult.noToken
            result.statusCode = rawResult.statusCode
            result.entity = response
            return result
        }
    }
}
