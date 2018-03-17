package com.kvteam.deliverytracker.core.webservice

import com.kvteam.deliverytracker.core.models.TaskInfo
import com.kvteam.deliverytracker.core.session.ISession
import com.kvteam.deliverytracker.core.webservice.viewmodels.TaskRequest
import com.kvteam.deliverytracker.core.webservice.viewmodels.TaskResponse
import java.util.*

class TaskWebservice(private val webservice: IWebservice,
                     private val session: ISession) : ITaskWebservice {

    private val tasksBaseUrl = "/api/tasks"

    override suspend fun createAsync(taskInfo: TaskInfo): NetworkResult<TaskResponse> {
        val request = TaskRequest()
        request.taskInfo = taskInfo

        val result = webservice.postAsync<TaskResponse>(
                "$tasksBaseUrl/createAsync",
                request,
                TaskResponse::class.java,
                true)
        return result
    }

    override suspend fun getAsync(id: UUID): NetworkResult<TaskResponse> {
        val result = webservice.getAsync<TaskResponse>(
                "$tasksBaseUrl/getAsync?id=$id",
                TaskResponse::class.java,
                true)
        return result
    }

    override suspend fun editAsync(taskInfo: TaskInfo): NetworkResult<TaskResponse> {
        val request = TaskRequest()
        request.taskInfo = taskInfo

        val result = webservice.postAsync<TaskResponse>(
                "$tasksBaseUrl/edit",
                request,
                TaskResponse::class.java,
                true)
        return result
    }

    override suspend fun changeStateAsync(id: UUID, newStateId: UUID): NetworkResult<TaskResponse> {
        val taskInfo = TaskInfo()
        taskInfo.id = id
        taskInfo.taskStateId = newStateId
        taskInfo.instanceId = session.user!!.instanceId
        val request = TaskRequest()
        request.taskInfo = taskInfo

        val result = webservice.postAsync<TaskResponse>(
                "$tasksBaseUrl/change_state",
                request,
                TaskResponse::class.java,
                true)
        return result
    }

}