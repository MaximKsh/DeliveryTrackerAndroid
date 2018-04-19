package com.kvteam.deliverytracker.core.webservice

import com.kvteam.deliverytracker.core.models.TaskPackage
import com.kvteam.deliverytracker.core.webservice.viewmodels.TaskResponse
import java.util.*

interface ITaskWebservice {
    suspend fun createAsync(taskInfo: TaskPackage): NetworkResult<TaskResponse>

    suspend fun getAsync(id: UUID): NetworkResult<TaskResponse>

    suspend fun editAsync(taskInfo: TaskPackage): NetworkResult<TaskResponse>

    suspend fun changeStateAsync(id: UUID, transitionId: UUID): NetworkResult<TaskResponse>
}