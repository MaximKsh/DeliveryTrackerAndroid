package com.kvteam.deliverytracker.performerapp.tasks

import com.google.gson.reflect.TypeToken
import com.kvteam.deliverytracker.core.common.EntityResult
import com.kvteam.deliverytracker.core.common.ILocalizationManager
import com.kvteam.deliverytracker.core.models.TaskModel
import com.kvteam.deliverytracker.core.storage.IStorage
import com.kvteam.deliverytracker.core.tasks.TaskState
import com.kvteam.deliverytracker.core.webservice.IWebservice
import java.util.*

class TaskRepository(
        private val webservice: IWebservice,
        private val storage: IStorage,
        private val localizationManager: ILocalizationManager)
    : ITaskRepository {
    override fun reserveTask(taskId: UUID): EntityResult<TaskModel?> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun takeTaskToWork(taskId: UUID): EntityResult<TaskModel?> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun performTask(taskId: UUID): EntityResult<TaskModel?> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun cancelTask(taskId: UUID): EntityResult<TaskModel?> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getTask(taskId: UUID): EntityResult<TaskModel?> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getMyTasks(resetCache: Boolean): EntityResult<List<TaskModel>?> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getUndistributedTasks(resetCache: Boolean): EntityResult<List<TaskModel>?> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


}