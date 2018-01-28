package com.kvteam.deliverytracker.managerapp.tasks

import com.kvteam.deliverytracker.core.common.EntityResult
import com.kvteam.deliverytracker.core.models.TaskModel
import com.kvteam.deliverytracker.core.models.User
import java.util.*

interface ITaskRepository {
    fun addTask(task: TaskModel): EntityResult<TaskModel?>
    fun cancelTask(taskId: UUID): EntityResult<TaskModel?>
    fun getAvailablePerformers(): EntityResult<List<User>?>

    fun getTask(taskId: UUID): EntityResult<TaskModel?>
    fun getMyTasks(resetCache: Boolean = false): EntityResult<List<TaskModel>?>
    fun getAllTasks(resetCache: Boolean = false): EntityResult<List<TaskModel>?>
}