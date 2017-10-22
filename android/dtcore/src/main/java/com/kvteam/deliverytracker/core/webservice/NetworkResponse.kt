package com.kvteam.deliverytracker.core.webservice

import com.kvteam.deliverytracker.core.models.ErrorListModel

data class NetworkResponse<T> (
        var fetched: Boolean = false,
        var statusCode: Int = 0,
        var responseEntity: T? = null,
        var errorList: ErrorListModel? = null)
