package com.kvteam.deliverytracker.performerapp.tasks

import com.google.gson.reflect.TypeToken
import com.kvteam.deliverytracker.core.models.TaskModel
import com.kvteam.deliverytracker.core.tasks.TaskState
import com.kvteam.deliverytracker.core.webservice.IWebservice
import java.util.*

class TaskRepository(
        private val webservice: IWebservice)
    : ITaskRepository {

    override fun reserveTask(taskId: UUID): TaskModel? {
        val requestObj = TaskModel(taskId)
        val result = this.webservice.post<TaskModel>(
                "/api/performer/reserve_task",
                requestObj,
                TaskModel::class.java)
        return result.responseEntity
    }

    override fun takeTaskToWork(taskId: UUID): TaskModel? {
        val requestObj = TaskModel(taskId)
        val result = this.webservice.post<TaskModel>(
                "/api/performer/take_task_to_work",
                requestObj,
                TaskModel::class.java)
        return result.responseEntity
    }

    override fun performTask(taskId: UUID): TaskModel? {
        val requestObj = TaskModel(taskId, state = TaskState.Performed.toString())
        val result = this.webservice.post<TaskModel>(
                "/api/performer/complete_task",
                requestObj,
                TaskModel::class.java)
        return result.responseEntity
    }

    override fun cancelTask(taskId: UUID): TaskModel? {
        val requestObj = TaskModel(taskId, state = TaskState.Cancelled.toString())
        val result = this.webservice.post<TaskModel>(
                "/api/performer/complete_task",
                requestObj,
                TaskModel::class.java)
        return result.responseEntity
    }

    override fun getTask(taskId: UUID): TaskModel? {
        val result = this.webservice.get<TaskModel>(
                "/api/performer/task/{$taskId}",
                TaskModel::class.java,
                true)
        return result.responseEntity
    }

    override fun getMyTasks(): List<TaskModel>? {
        val result = this.webservice.get<List<TaskModel>>(
                "/api/performer/my_tasks",
                object : TypeToken<ArrayList<TaskModel>>(){}.type,
                true)
        return result.responseEntity
    }

    override fun getUndistributedTasks(): List<TaskModel>? {
        val result = this.webservice.get<List<TaskModel>>(
                "/api/performer/undistributed_tasks",
                object : TypeToken<ArrayList<TaskModel>>(){}.type,
                true)
        return result.responseEntity
    }
}