package com.kvteam.deliverytracker.performerapp.ui.main.userslist

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.databinding.ObservableField
import android.support.v7.widget.LinearLayoutManager
import com.kvteam.deliverytracker.performerapp.PerformerApplication
import javax.inject.Inject

class UsersListViewModel
@Inject constructor(app: PerformerApplication): ViewModel() {
    var header = ObservableField<String>()

    var layoutManager = ObservableField<LinearLayoutManager>(
            LinearLayoutManager(app, LinearLayoutManager.VERTICAL, false))
}