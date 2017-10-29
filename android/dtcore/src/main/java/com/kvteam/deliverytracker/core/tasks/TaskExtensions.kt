package com.kvteam.deliverytracker.core.tasks

fun String.toTaskState(): TaskState? {
    return when(this){
        TaskState.NewUndistributed.simpleName -> TaskState.NewUndistributed
        TaskState.New.simpleName -> TaskState.New
        TaskState.InWork.simpleName -> TaskState.InWork
        TaskState.Performed.simpleName -> TaskState.Performed
        TaskState.Cancelled.simpleName -> TaskState.Cancelled
        TaskState.CancelledByManager.simpleName -> TaskState.CancelledByManager
        else -> null
    }
}