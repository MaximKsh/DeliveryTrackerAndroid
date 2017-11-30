package com.kvteam.deliverytracker.core.common

import java.util.*

open class SimpleResult(
        val success: Boolean,
        val fromNetwork : Boolean,
        val fromCache : Boolean,
        val errorChainId: UUID?) {
}