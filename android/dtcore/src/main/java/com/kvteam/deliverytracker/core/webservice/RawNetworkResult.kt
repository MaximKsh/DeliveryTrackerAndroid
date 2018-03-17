package com.kvteam.deliverytracker.core.webservice

import com.kvteam.deliverytracker.core.common.EntityResult

open class RawNetworkResult(
        open val fetched: Boolean = false,
        open val statusCode: Int = 0
) : EntityResult<String>()
