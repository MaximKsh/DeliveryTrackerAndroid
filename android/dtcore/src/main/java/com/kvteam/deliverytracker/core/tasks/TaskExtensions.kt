package com.kvteam.deliverytracker.core.tasks

import com.kvteam.deliverytracker.core.common.EMPTY_STRING
import com.kvteam.deliverytracker.core.common.ILocalizationManager
import com.kvteam.deliverytracker.core.models.TaskInfo
import java.util.*

fun getTaskStateById(id: UUID?) : TaskState? {
    return when(id){
        TaskState.Preparing.id -> TaskState.Preparing
        TaskState.Queue.id -> TaskState.Queue
        TaskState.Waiting.id -> TaskState.Waiting
        TaskState.IntoWork.id -> TaskState.IntoWork
        TaskState.Delivered.id -> TaskState.Delivered
        TaskState.Complete.id -> TaskState.Complete
        TaskState.Revoked.id -> TaskState.Revoked
        else -> null
    }
}

fun UUID.getTaskState(): TaskState? {
    return getTaskStateById(this)
}

fun TaskInfo.getTaskState(): TaskState? {
    return getTaskStateById(this.taskStateId)
}

fun TaskInfo.getTaskStateCaption(lm: ILocalizationManager) : String {
    val taskState = this.getTaskState() ?: return EMPTY_STRING
    return lm.getString(taskState.stateCaption)
}

