package com.kvteam.deliverytracker.performerapp.ui.confirm

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.databinding.ObservableField
import com.kvteam.deliverytracker.core.ui.DefaultTextWatcher
import javax.inject.Inject

class ConfirmDataViewModel
    @Inject constructor(): ViewModel() {
    val surname = MutableLiveData<String>()
    val name = MutableLiveData<String>()
    val phoneNumber = MutableLiveData<String>()
    val surnameWatcher = DefaultTextWatcher(surname)
    val nameWatcher = DefaultTextWatcher(name)
    val phoneNumberWatcher = DefaultTextWatcher(phoneNumber)

    var openedFromSettings = false
}