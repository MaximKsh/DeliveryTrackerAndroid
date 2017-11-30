package com.kvteam.deliverytracker.core.common

import java.util.*

open class EntityResult<out T : Any?>(
        val entity : T,
        fromNetwork : Boolean,
        fromCache : Boolean,
        errorChainId: UUID?) : SimpleResult(
            entity != null,
            fromNetwork,
            fromCache,
            errorChainId)