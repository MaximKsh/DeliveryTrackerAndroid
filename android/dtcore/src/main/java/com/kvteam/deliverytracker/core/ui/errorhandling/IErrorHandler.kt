package com.kvteam.deliverytracker.core.ui.errorhandling

import com.kvteam.deliverytracker.core.common.ErrorListResult
import com.kvteam.deliverytracker.core.webservice.NetworkResult
import com.kvteam.deliverytracker.core.webservice.RawNetworkResult
import com.kvteam.deliverytracker.core.webservice.viewmodels.ResponseBase

interface IErrorHandler {
    fun handle(errorListResult: ErrorListResult,
               customAction: ((ctx:ErrorHandlerContext) -> Boolean)? = null): Boolean

    fun <T : ResponseBase> handle(networkResult: NetworkResult<T>,
                                  customAction: ((ctx:ErrorHandlerContext) -> Boolean)? = null): Boolean

    fun handle(networkResult: RawNetworkResult,
               customAction: ((ctx:ErrorHandlerContext) -> Boolean)? = null): Boolean
}