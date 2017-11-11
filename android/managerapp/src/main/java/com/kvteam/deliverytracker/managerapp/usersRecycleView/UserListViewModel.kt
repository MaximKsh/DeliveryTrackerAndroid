package com.kvteam.deliverytracker.managerapp.usersRecycleView

import android.arch.lifecycle.ViewModel
import android.databinding.ObservableArrayList
import android.databinding.ObservableField
import android.databinding.ObservableList
import com.kvteam.deliverytracker.core.models.UserModel
import javax.inject.Inject

class UsersListViewModel
@Inject constructor(): ViewModel() {
    var isInEditMode = ObservableField<Boolean>(false)
    var selectedUsersList = ObservableArrayList<String>()
}