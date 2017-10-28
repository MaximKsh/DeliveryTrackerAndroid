package com.kvteam.deliverytracker.performerapp.ui.main.taskslist

import android.arch.lifecycle.ViewModel
import android.databinding.ObservableField
import javax.inject.Inject

class TasksListViewModel
@Inject constructor(): ViewModel() {
    var header = ObservableField<String>()
}