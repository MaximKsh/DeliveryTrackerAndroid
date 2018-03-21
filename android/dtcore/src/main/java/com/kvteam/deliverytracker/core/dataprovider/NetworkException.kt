package com.kvteam.deliverytracker.core.dataprovider

import com.kvteam.deliverytracker.core.webservice.NetworkResult

class NetworkException(
        val result: NetworkResult<*>
) : Exception()