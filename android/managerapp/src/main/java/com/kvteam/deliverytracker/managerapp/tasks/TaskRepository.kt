package com.kvteam.deliverytracker.managerapp.tasks

import com.google.gson.reflect.TypeToken
import com.kvteam.deliverytracker.core.common.EntityResult
import com.kvteam.deliverytracker.core.common.ErrorItem
import com.kvteam.deliverytracker.core.common.IErrorManager
import com.kvteam.deliverytracker.core.common.ILocalizationManager
import com.kvteam.deliverytracker.core.models.TaskModel
import com.kvteam.deliverytracker.core.models.UserModel
import com.kvteam.deliverytracker.core.storage.IStorage
import com.kvteam.deliverytracker.core.webservice.IWebservice
import com.kvteam.deliverytracker.core.webservice.NetworkResponse
import com.kvteam.deliverytracker.managerapp.R
import java.util.*

class TaskRepository(
        private val webservice: IWebservice,
        private val storage: IStorage,
        private val errorManager: IErrorManager,
        private val localizationManager: ILocalizationManager): ITaskRepository {
    override fun addTask(task: TaskModel): EntityResult<TaskModel?> {
        val result = webservice.post<TaskModel>(
                "/api/manager/add_task",
                task,
                TaskModel::class.java,
                true)
        val chain = buildChain(result)
        return EntityResult(
                result.responseEntity,
                result.fetched,
                false,
                chain)
    }

    override fun cancelTask(taskId: UUID): EntityResult<TaskModel?> {
        val requestObj = TaskModel(taskId)
        val result = webservice.post<TaskModel>(
                "/api/manager/cancel_task",
                requestObj,
                TaskModel::class.java,
                true)
        val chain = buildChain(result)
        return EntityResult(
                result.responseEntity,
                result.fetched,
                false,
                chain)
    }

    override fun getAvailablePerformers(): EntityResult<List<UserModel>?> {
        val result = webservice.get<List<UserModel>>(
                "/api/manager/available_performers",
                object : TypeToken<ArrayList<UserModel>>(){}.type,
                true)
        val chain = buildChain(result)
        return EntityResult(
                result.responseEntity,
                result.fetched,
                false,
                chain)
    }


    override fun getTask(taskId: UUID): EntityResult<TaskModel?> {
        val result = webservice.get<TaskModel>(
                "/api/manager/task/{$taskId}",
                TaskModel::class.java,
                true)
        val chain = buildChain(result)
        return EntityResult(
                result.responseEntity,
                result.fetched,
                false,
                chain)
    }

    override fun getMyTasks(resetCache: Boolean): EntityResult<List<TaskModel>?> {
        val cacheKey = "manager_my_tasks"

        if(resetCache) {
            storage.deleteTasks(cacheKey)
        } else {
            val tasks = storage.getTasks(cacheKey)
            if(tasks.isNotEmpty()) {
                return EntityResult(tasks, false, true, null)
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
        val chain = buildChain(result)
        return EntityResult(
                result.responseEntity,
                result.fetched,
                false,
                chain)
    }

    override fun getAllTasks(resetCache: Boolean): EntityResult<List<TaskModel>?> {
        val cacheKey = "manager_all_tasks"

        if(resetCache) {
            storage.deleteTasks(cacheKey)
        } else {
            val tasks = storage.getTasks(cacheKey)
            if(tasks.isNotEmpty()) {
                return EntityResult(tasks, false, true, null)
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
        val chain = buildChain(result)
        return EntityResult(
                result.responseEntity,
                result.fetched,
                false,
                chain)
    }

    private fun <T> buildChain(nr: NetworkResponse<T>?) : UUID? {
        if(nr == null) {
            return null
        }
        val items = nr.errorList?.errors?.toList()

        val chainBuilder = errorManager
                .begin()
                .alias(localizationManager.getString(R.string.ManagerApp_TaskRepository_Error_ErrorTitle))

        if(!nr.fetched) {
            chainBuilder.add(ErrorItem(localizationManager.getString(R.string.Core_Error_NetworkError)))
        } else if (items != null){
            for(it in items) {
                chainBuilder.add(ErrorItem(localizationManager.getString(it.message)))
            }
        }
        return chainBuilder.complete()
    }
}