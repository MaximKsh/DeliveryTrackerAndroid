package com.kvteam.deliverytracker.performerapp.tasks

import com.kvteam.deliverytracker.core.common.EntityResult
import com.kvteam.deliverytracker.core.models.TaskModel
import java.util.*

interface ITaskRepository {

    fun reserveTask(taskId: UUID): EntityResult<TaskModel?>
    fun takeTaskToWork(taskId: UUID): EntityResult<TaskModel?>
    fun performTask(taskId: UUID): EntityResult<TaskModel?>
    fun cancelTask(taskId: UUID): EntityResult<TaskModel?>

    fun getTask(taskId: UUID): EntityResult<TaskModel?>
    fun getMyTasks(resetCache: Boolean = false): EntityResult<List<TaskModel>?>
    fun getUndistributedTasks(resetCache: Boolean = false): EntityResult<List<TaskModel>?>
}