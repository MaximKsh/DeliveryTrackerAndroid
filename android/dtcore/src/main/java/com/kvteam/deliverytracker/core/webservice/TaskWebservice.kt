package com.kvteam.deliverytracker.core.webservice

import com.kvteam.deliverytracker.core.models.TaskPackage
import com.kvteam.deliverytracker.core.webservice.viewmodels.TaskRequest
import com.kvteam.deliverytracker.core.webservice.viewmodels.TaskResponse
import java.util.*

class TaskWebservice(private val webservice: IWebservice) : ITaskWebservice {

    private val tasksBaseUrl = "/api/tasks"

    override suspend fun createAsync(taskPackage: TaskPackage): NetworkResult<TaskResponse> {
        val request = TaskRequest()
        request.taskPackage = taskPackage

        val result = webservice.postAsync<TaskResponse>(
                "$tasksBaseUrl/create",
                request,
                TaskResponse::class.java,
                true)
        return result
    }

    override suspend fun getAsync(id: UUID): NetworkResult<TaskResponse> {
        val result = webservice.getAsync<TaskResponse>(
                "$tasksBaseUrl/get?id=$id",
                TaskResponse::class.java,
                true)
        return result
    }

    override suspend fun editAsync(taskInfo: TaskPackage): NetworkResult<TaskResponse> {
        val request = TaskRequest()
        request.taskPackage = taskInfo

        val result = webservice.postAsync<TaskResponse>(
                "$tasksBaseUrl/edit",
                request,
                TaskResponse::class.java,
                true)
        return result
    }

    override suspend fun changeStateAsync(id: UUID, transitionId: UUID): NetworkResult<TaskResponse> {
        val request = TaskRequest()
        request.id = id
        request.transitionId = transitionId

        val result = webservice.postAsync<TaskResponse>(
                "$tasksBaseUrl/change_state",
                request,
                TaskResponse::class.java,
                true)
        return result
    }

}