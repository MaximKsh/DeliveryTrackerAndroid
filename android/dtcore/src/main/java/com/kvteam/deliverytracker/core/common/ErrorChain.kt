package com.kvteam.deliverytracker.core.common

import java.util.*

data class ErrorChain(
        val id: UUID,
        val alias: String,
        val items: List<ErrorItem>)