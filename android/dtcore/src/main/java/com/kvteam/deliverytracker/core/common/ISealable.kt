package com.kvteam.deliverytracker.core.common

interface ISealable {
    val seal: Seal

    fun seal() {
        checkSeal()
        seal.seal()
    }

    fun checkSeal() {
        if(seal.seal.get()) {
            throw ObjectSealedException()
        }
    }
}