package com.kvteam.deliverytracker.managerapp.usersRecycleView

import android.arch.lifecycle.ViewModel
import android.databinding.ObservableField
import javax.inject.Inject

class UsersListViewModel
@Inject constructor(): ViewModel() {
    var isInEditMode = ObservableField<Boolean>()
}