package com.kvteam.deliverytracker.managerapp.tasks

import com.google.gson.reflect.TypeToken
import com.kvteam.deliverytracker.core.models.TaskModel
import com.kvteam.deliverytracker.core.models.UserModel
import com.kvteam.deliverytracker.core.storage.IStorage
import com.kvteam.deliverytracker.core.webservice.IWebservice
import java.util.*

class TaskRepository(
        private val webservice: IWebservice,
        private val storage: IStorage): ITaskRepository {
    override fun addTask(task: TaskModel): TaskModel? {
        val result = webservice.post<TaskModel>(
                "/api/manager/add_task",
                task,
                TaskModel::class.java,
                true)
        return result.responseEntity
    }

    override fun cancelTask(taskId: UUID): TaskModel? {
        val requestObj = TaskModel(taskId)
        val result = webservice.post<TaskModel>(
                "/api/manager/cancel_task",
                requestObj,
                TaskModel::class.java,
                true)
        return result.responseEntity
    }

    override fun getAvailablePerformers(): List<UserModel>? {
        val result = webservice.get<List<UserModel>>(
                "/api/manager/available_performers",
                object : TypeToken<ArrayList<UserModel>>(){}.type,
                true)
       return result.responseEntity
    }


    override fun getTask(taskId: UUID): TaskModel? {
        val result = webservice.get<TaskModel>(
                "/api/manager/task/{$taskId}",
                TaskModel::class.java,
                true)
        return result.responseEntity
    }

    override fun getMyTasks(resetCache: Boolean): List<TaskModel>? {
        val cacheKey = "manager_my_tasks"

        if(resetCache) {
            storage.deleteTasks(cacheKey)
        } else {
            val tasks = storage.getTasks(cacheKey)
            if(tasks.isNotEmpty()) {
                return tasks
            }
        }

        val result = webservice.get<List<TaskModel>>(
                "/api/manager/my_tasks",
                object : TypeToken<ArrayList<TaskModel>>(){}.type,
                true)
        val newTasks = result.responseEntity
        if(result.statusCode == 200
                && newTasks != null) {
            storage.setTasks(cacheKey, newTasks)
        }
        return newTasks
    }

    override fun getAllTasks(resetCache: Boolean): List<TaskModel>? {
        val cacheKey = "manager_all_tasks"

        if(resetCache) {
            storage.deleteTasks(cacheKey)
        } else {
            val tasks = storage.getTasks(cacheKey)
            if(tasks.isNotEmpty()) {
                return tasks
            }
        }

        val result = webservice.get<List<TaskModel>>(
                "/api/manager/tasks",
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