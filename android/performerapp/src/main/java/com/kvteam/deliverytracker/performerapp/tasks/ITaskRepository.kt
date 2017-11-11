package com.kvteam.deliverytracker.performerapp.tasks

import com.kvteam.deliverytracker.core.models.TaskModel
import java.util.UUID

interface ITaskRepository {

    fun reserveTask(taskId: UUID): TaskModel?
    fun takeTaskToWork(taskId: UUID): TaskModel?
    fun performTask(taskId: UUID): TaskModel?
    fun cancelTask(taskId: UUID): TaskModel?

    fun getTask(taskId: UUID): TaskModel?
    fun getMyTasks(resetCache: Boolean = false): List<TaskModel>?
    fun getUndistributedTasks(resetCache: Boolean = false): List<TaskModel>?
}