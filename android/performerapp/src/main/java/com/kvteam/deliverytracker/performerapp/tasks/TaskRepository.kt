package com.kvteam.deliverytracker.performerapp.tasks

import com.google.gson.reflect.TypeToken
import com.kvteam.deliverytracker.core.models.TaskModel
import com.kvteam.deliverytracker.core.storage.IStorage
import com.kvteam.deliverytracker.core.tasks.TaskState
import com.kvteam.deliverytracker.core.webservice.IWebservice
import java.util.*

class TaskRepository(
        private val webservice: IWebservice,
        private val storage: IStorage)
    : ITaskRepository {

    override fun reserveTask(taskId: UUID): TaskModel? {
        val requestObj = TaskModel(taskId)
        val result = webservice.post<TaskModel>(
                "/api/performer/reserve_task",
                requestObj,
                TaskModel::class.java,
                true)
        return result.responseEntity
    }

    override fun takeTaskToWork(taskId: UUID): TaskModel? {
        val requestObj = TaskModel(taskId)
        val result = webservice.post<TaskModel>(
                "/api/performer/take_task_to_work",
                requestObj,
                TaskModel::class.java,
                true)
        return result.responseEntity
    }

    override fun performTask(taskId: UUID): TaskModel? {
        val requestObj = TaskModel(taskId, state = TaskState.Performed.simpleName)
        val result = webservice.post<TaskModel>(
                "/api/performer/complete_task",
                requestObj,
                TaskModel::class.java,
                true)
        return result.responseEntity
    }

    override fun cancelTask(taskId: UUID): TaskModel? {
        val requestObj = TaskModel(taskId, state = TaskState.Cancelled.simpleName)
        val result = webservice.post<TaskModel>(
                "/api/performer/complete_task",
                requestObj,
                TaskModel::class.java,
                true)
        return result.responseEntity
    }

    override fun getTask(taskId: UUID): TaskModel? {
        val result = webservice.get<TaskModel>(
                "/api/performer/task/{$taskId}",
                TaskModel::class.java,
                true)
        return result.responseEntity
    }

    override fun getMyTasks(resetCache: Boolean): List<TaskModel>? {
        val cacheKey = "my_tasks"
        if(resetCache) {
            storage.deleteTasks(cacheKey)
        } else {
            val tasks = storage.getTasks(cacheKey)
            if(tasks.isNotEmpty()) {
                return tasks
            }
        }

        val result = webservice.get<List<TaskModel>>(
                "/api/performer/my_tasks",
                object : TypeToken<ArrayList<TaskModel>>(){}.type,
                true)
        val newTasks = result.responseEntity
        if(result.statusCode == 200
                && newTasks != null) {
            storage.setTasks(cacheKey, newTasks)
        }
        return newTasks
    }

    override fun getUndistributedTasks(resetCache: Boolean): List<TaskModel>? {
        val cacheKey = "undistributed_tasks"
        if(resetCache) {
            storage.deleteTasks(cacheKey)
        } else {
            val tasks = storage.getTasks(cacheKey)
            if(tasks.isNotEmpty()) {
                return tasks
            }
        }

        val result = webservice.get<List<TaskModel>>(
                "/api/performer/undistributed_tasks",
                object : TypeToken<ArrayList<TaskModel>>(){}.type,
                true)
        val newTasks = result.responseEntity
        if(result.statusCode == 200
                && newTasks != null) {
            storage.setTasks(cacheKey, newTasks)
        }
        return newTasks
    }
}