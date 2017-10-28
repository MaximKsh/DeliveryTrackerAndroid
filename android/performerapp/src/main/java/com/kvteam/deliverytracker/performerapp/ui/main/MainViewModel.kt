package com.kvteam.deliverytracker.performerapp.ui.main

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.databinding.ObservableInt
import android.view.MenuItem
import com.kvteam.deliverytracker.core.session.ISession
import com.kvteam.deliverytracker.core.ui.DefaultNavigationItemSelectedListener
import javax.inject.Inject

class MainViewModel
@Inject constructor() : ViewModel() {
    val selectedBottomMenu = MutableLiveData<Int>()
    val selectedBottomMenuListener = DefaultNavigationItemSelectedListener(selectedBottomMenu)

}