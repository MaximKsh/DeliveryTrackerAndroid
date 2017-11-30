package com.kvteam.deliverytracker.core.common

import java.util.*

class ErrorManager: IErrorManager {
    private val chains = mutableMapOf<UUID, ErrorChain>()

    override fun begin() : ErrorChainBuilder = ErrorChainBuilder(chains)

    override fun get(id: UUID): ErrorChain? {
        return chains[id]
    }

    override fun getAndRemove(id: UUID): ErrorChain? {
        return chains.remove(id)
    }

    override fun getAll(): List<ErrorChain> {
        return chains.values.toList()
    }

    override fun getAllAndClear(): List<ErrorChain> {
        val items = chains.values.toList()
        chains.clear()
        return items
    }

    override fun remove(id: UUID): Boolean {
        return chains.remove(id) != null
    }

    override fun clear(): Int {
        val size = chains.size
        chains.clear()
        return size
    }

}