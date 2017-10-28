package com.kvteam.deliverytracker.performerapp.ui.main.userslist

import android.arch.lifecycle.ViewModel
import android.databinding.ObservableField
import javax.inject.Inject

class UsersListViewModel
@Inject constructor(): ViewModel() {
    var header = ObservableField<String>()
}