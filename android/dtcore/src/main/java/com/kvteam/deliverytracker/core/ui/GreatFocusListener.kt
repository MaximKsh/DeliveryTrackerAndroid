package com.kvteam.deliverytracker.core.ui

import android.view.View
import android.widget.EditText

class GreatFocusListener : View.OnFocusChangeListener {
    private val listeners = arrayListOf<View.OnFocusChangeListener>()

    override fun onFocusChange(view: View?, focused: Boolean) {
        listeners.forEach{ it.onFocusChange(view, focused) }
    }

    fun addListener(listener: View.OnFocusChangeListener) {
        listeners.add(listener)
    }

    fun removeListener(listener: View.OnFocusChangeListener) {
        listeners.remove(listener)
    }
}

fun EditText.addOnFocusChangeListener(listener: View.OnFocusChangeListener) {
    this.onFocusChangeListener = when (this.onFocusChangeListener) {
        null -> GreatFocusListener()
        !is GreatFocusListener -> {
            val gListener = GreatFocusListener()
            gListener.addListener(this.onFocusChangeListener)
            gListener
        }
        else -> this.onFocusChangeListener
    }

    (this.onFocusChangeListener as GreatFocusListener).addListener(listener)
}

fun EditText.removeOnFocusChangeListener(listener: View.OnFocusChangeListener) {
    (this.onFocusChangeListener as? GreatFocusListener)?.removeListener(listener)
}