package com.kvteam.deliverytracker.managerapp.tasks

import com.kvteam.deliverytracker.core.models.TaskModel
import com.kvteam.deliverytracker.core.models.UserModel
import java.util.*

interface ITaskRepository {
    fun addTask(task: TaskModel): TaskModel?
    fun cancelTask(taskId: UUID): TaskModel?
    fun getAvailablePerformers(): List<UserModel>?

    fun getTask(taskId: UUID): TaskModel?
    fun getMyTasks(resetCache: Boolean = false): List<TaskModel>?
    fun getAllTasks(resetCache: Boolean = false): List<TaskModel>?
}