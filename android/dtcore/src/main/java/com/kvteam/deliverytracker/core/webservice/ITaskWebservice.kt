package com.kvteam.deliverytracker.core.webservice

import com.kvteam.deliverytracker.core.models.TaskInfo
import com.kvteam.deliverytracker.core.webservice.viewmodels.TaskResponse
import java.util.*

interface ITaskWebservice {
    suspend fun createAsync(taskInfo: TaskInfo): NetworkResult<TaskResponse>

    suspend fun getAsync(id: UUID): NetworkResult<TaskResponse>

    suspend fun editAsync(taskInfo: TaskInfo): NetworkResult<TaskResponse>

    suspend fun changeStateAsync(id: UUID, newStateId: UUID): NetworkResult<TaskResponse>
}