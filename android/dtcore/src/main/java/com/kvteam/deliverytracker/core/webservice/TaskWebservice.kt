package com.kvteam.deliverytracker.core.webservice

import com.kvteam.deliverytracker.core.models.TaskInfo
import com.kvteam.deliverytracker.core.session.ISession
import com.kvteam.deliverytracker.core.webservice.viewmodels.TaskRequest
import com.kvteam.deliverytracker.core.webservice.viewmodels.TaskResponse
import java.util.*

class TaskWebservice(private val webservice: IWebservice,
                     private val session: ISession) : ITaskWebservice {

    private val tasksBaseUrl = "/api/tasks"

    override fun create(taskInfo: TaskInfo): NetworkResult<TaskResponse> {
        val request = TaskRequest()
        request.taskInfo = taskInfo

        val result = webservice.post<TaskResponse>(
                "$tasksBaseUrl/create",
                request,
                TaskResponse::class.java,
                true)
        return result
    }

    override fun get(id: UUID): NetworkResult<TaskResponse> {
        val result = webservice.get<TaskResponse>(
                "$tasksBaseUrl/get?id=$id",
                TaskResponse::class.java,
                true)
        return result
    }

    override fun edit(taskInfo: TaskInfo): NetworkResult<TaskResponse> {
        val request = TaskRequest()
        request.taskInfo = taskInfo

        val result = webservice.post<TaskResponse>(
                "$tasksBaseUrl/edit",
                request,
                TaskResponse::class.java,
                true)
        return result
    }

    override fun changeState(id: UUID, newStateId: UUID): NetworkResult<TaskResponse> {
        val taskInfo = TaskInfo()
        taskInfo.id = id
        taskInfo.taskStateId = newStateId
        taskInfo.instanceId = session.instanceId
        val request = TaskRequest()
        request.taskInfo = taskInfo

        val result = webservice.post<TaskResponse>(
                "$tasksBaseUrl/change_state",
                request,
                TaskResponse::class.java,
                true)
        return result
    }

}