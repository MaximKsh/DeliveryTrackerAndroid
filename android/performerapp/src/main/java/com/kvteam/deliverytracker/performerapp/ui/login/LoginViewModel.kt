package com.kvteam.deliverytracker.performerapp.ui.login

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.databinding.ObservableBoolean
import android.databinding.ObservableField
import com.kvteam.deliverytracker.core.ui.DefaultTextWatcher
import com.kvteam.deliverytracker.core.session.ISession
import javax.inject.Inject

class LoginViewModel
@Inject constructor() : ViewModel() {
    val username = MutableLiveData<String>()
    val password = MutableLiveData<String>()
    val usernameWatcher = DefaultTextWatcher(username)
    val passwordWatcher = DefaultTextWatcher(password)

    var operationPending = ObservableBoolean(false)

    var openedFromSettings = false
}