package com.kvteam.deliverytracker.performerapp.ui.main.task

import android.arch.lifecycle.ViewModel
import android.databinding.ObservableField
import javax.inject.Inject

class TaskViewModel
@Inject constructor(): ViewModel() {
    var number = ObservableField<String>()
    var details = ObservableField<String>()
    var shippingDesc = ObservableField<String>()
    var address = ObservableField<String>()
    var state = ObservableField<String>()
}