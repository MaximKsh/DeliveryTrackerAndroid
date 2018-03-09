package com.kvteam.deliverytracker.core.webservice

import com.kvteam.deliverytracker.core.models.TaskInfo
import com.kvteam.deliverytracker.core.webservice.viewmodels.TaskResponse
import java.util.*

interface ITaskWebservice {
    fun create(taskInfo: TaskInfo): NetworkResult<TaskResponse>

    fun get(id: UUID): NetworkResult<TaskResponse>

    fun edit(taskInfo: TaskInfo): NetworkResult<TaskResponse>

    fun changeState(id: UUID, newStateId: UUID): NetworkResult<TaskResponse>
}