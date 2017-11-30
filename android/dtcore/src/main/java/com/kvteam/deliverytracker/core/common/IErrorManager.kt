package com.kvteam.deliverytracker.core.common

import java.util.*

interface IErrorManager {
    fun begin() : ErrorChainBuilder

    fun get(id: UUID) : ErrorChain?
    fun getAndRemove(id: UUID) : ErrorChain?
    fun getAll() : List<ErrorChain>
    fun getAllAndClear() : List<ErrorChain>
    fun remove(id: UUID) : Boolean
    fun clear() : Int
}