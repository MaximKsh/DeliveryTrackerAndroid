package com.kvteam.deliverytracker.managerapp.ui.main.taskslist

import com.kvteam.deliverytracker.core.ui.DeliveryTrackerFragment
import java.util.*

abstract class BaseTaskPageFragment : DeliveryTrackerFragment() {
    private val taskIdKey = "task"
    protected var taskId
        get() = arguments?.getSerializable(taskIdKey)!! as UUID
        set(value) = arguments?.putSerializable(taskIdKey, value)!!

    fun setTask(id: UUID?) {
        this.taskId = id ?: UUID.randomUUID()
    }

    open fun shouldDeleteDirty() = true
}