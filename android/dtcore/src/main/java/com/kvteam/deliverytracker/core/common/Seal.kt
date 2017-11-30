package com.kvteam.deliverytracker.core.common

import java.util.concurrent.atomic.AtomicBoolean

class Seal {
    var seal : AtomicBoolean = AtomicBoolean(false)
        private set

    fun seal() {
        seal.set(true)
    }
}