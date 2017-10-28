package com.kvteam.deliverytracker.core.ui

import android.arch.lifecycle.MutableLiveData
import android.databinding.ObservableField
import android.text.Editable
import android.text.TextWatcher
import java.util.*

class DefaultTextWatcher(private val field: MutableLiveData<String>): TextWatcher  {
    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
    override fun afterTextChanged(s: Editable?) {}

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        if (!Objects.equals(field.value, s.toString())) {
            field.value = s.toString()
        }
    }
}