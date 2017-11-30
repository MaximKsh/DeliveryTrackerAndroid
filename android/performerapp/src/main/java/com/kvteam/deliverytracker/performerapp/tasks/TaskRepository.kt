package com.kvteam.deliverytracker.performerapp.tasks

import com.google.gson.reflect.TypeToken
import com.kvteam.deliverytracker.core.common.EntityResult
import com.kvteam.deliverytracker.core.common.ErrorItem
import com.kvteam.deliverytracker.core.common.IErrorManager
import com.kvteam.deliverytracker.core.common.ILocalizationManager
import com.kvteam.deliverytracker.core.models.TaskModel
import com.kvteam.deliverytracker.core.storage.IStorage
import com.kvteam.deliverytracker.core.tasks.TaskState
import com.kvteam.deliverytracker.core.webservice.IWebservice
import com.kvteam.deliverytracker.core.webservice.NetworkResponse
import com.kvteam.deliverytracker.performerapp.R
import java.util.*

class TaskRepository(
        private val webservice: IWebservice,
        private val storage: IStorage,
        private val errorManager: IErrorManager,
        private val localizationManager: ILocalizationManager)
    : ITaskRepository {

    override fun reserveTask(taskId: UUID): EntityResult<TaskModel?> {
        val requestObj = TaskModel(taskId)
        val result = webservice.post<TaskModel>(
                "/api/performer/reserve_task",
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

    override fun takeTaskToWork(taskId: UUID): EntityResult<TaskModel?> {
        val requestObj = TaskModel(taskId)
        val result = webservice.post<TaskModel>(
                "/api/performer/take_task_to_work",
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

    override fun performTask(taskId: UUID): EntityResult<TaskModel?> {
        val requestObj = TaskModel(taskId, state = TaskState.Performed.simpleName)
        val result = webservice.post<TaskModel>(
                "/api/performer/complete_task",
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

    override fun cancelTask(taskId: UUID): EntityResult<TaskModel?> {
        val requestObj = TaskModel(taskId, state = TaskState.Cancelled.simpleName)
        val result = webservice.post<TaskModel>(
                "/api/performer/complete_task",
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

    override fun getTask(taskId: UUID): EntityResult<TaskModel?> {
        val result = webservice.get<TaskModel>(
                "/api/performer/task/{$taskId}",
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
        val cacheKey = "my_tasks"
        if(resetCache) {
            storage.deleteTasks(cacheKey)
        } else {
            val tasks = storage.getTasks(cacheKey)
            if(tasks.isNotEmpty()) {
                return EntityResult(tasks, false, true, null)
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
        val chain = buildChain(result)
        return EntityResult(
                result.responseEntity,
                result.fetched,
                false,
                chain)
    }

    override fun getUndistributedTasks(resetCache: Boolean): EntityResult<List<TaskModel>?> {
        val cacheKey = "undistributed_tasks"
        if(resetCache) {
            storage.deleteTasks(cacheKey)
        } else {
            val tasks = storage.getTasks(cacheKey)
            if(tasks.isNotEmpty()) {
                return EntityResult(tasks, false, true, null)
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
                .alias(localizationManager.getString(R.string.PerformerApp_TaskRepository_Error_ErrorTitle))

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