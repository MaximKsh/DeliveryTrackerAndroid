package com.kvteam.deliverytracker.performerapp.ui.main.task

import android.arch.lifecycle.ViewModel
import android.databinding.Observable
import android.databinding.ObservableBoolean
import android.databinding.ObservableField
import com.kvteam.deliverytracker.core.models.TaskModel
import com.kvteam.deliverytracker.core.tasks.TaskState
import com.kvteam.deliverytracker.core.tasks.toTaskState
import javax.inject.Inject

class TaskViewModel
@Inject constructor(): ViewModel() {
    var task = ObservableField<TaskModel>()
    var operationPending = ObservableBoolean(false)
    var taskState = ObservableField<TaskState>()

    init {
        task.addOnPropertyChangedCallback(object: Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(p0: Observable?, p1: Int) {
                if(p0 is ObservableField<*>) {
                    val obj = p0.get()
                    if(obj is TaskModel) {
                        val state = obj.state?.toTaskState()
                        taskState.set(state)
                    }
                }
            }
        })
    }
}