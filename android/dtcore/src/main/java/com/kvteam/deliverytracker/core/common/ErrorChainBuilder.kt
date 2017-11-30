package com.kvteam.deliverytracker.core.common

import java.util.*

class ErrorChainBuilder (
        private val chains: MutableMap<UUID, ErrorChain>) {

    private val items = mutableListOf<ErrorItem>()
    var id = UUID.randomUUID()!!
        private set
    var alias: String = EMPTY_STRING
        private set

    fun id(id: UUID): ErrorChainBuilder {
        this.id = id
        return this
    }

    fun alias(alias: String): ErrorChainBuilder {
        this.alias = alias
        return this
    }

    fun add(error: ErrorItem): ErrorChainBuilder {
        items.add(error)
        return this
    }

    fun complete() : UUID {
        val chain = ErrorChain(id, alias, items)
        chains.put(id, chain)
        return id
    }
}