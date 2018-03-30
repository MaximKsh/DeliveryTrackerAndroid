package com.kvteam.deliverytracker.core.ui

class UIState {
    private val fragmentStates = mutableMapOf<String, MutableMap<String, Any?>>()

    fun forFragment(fragment: DeliveryTrackerFragment) : MutableMap<String, Any?> {
        return fragmentStates.getOrPut(fragment.name, { mutableMapOf() })
    }

    fun forFragment(fragment: String) : MutableMap<String, Any?> {
        return fragmentStates.getOrPut(fragment, { mutableMapOf() })
    }
}