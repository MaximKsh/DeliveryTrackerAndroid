package com.kvteam.deliverytracker.performerapp.ui.main.performerslist

import android.arch.lifecycle.ViewModel
import android.databinding.ObservableField
import javax.inject.Inject

class PerformersListViewModel
@Inject constructor(): ViewModel() {
    var header = ObservableField<String>()
}