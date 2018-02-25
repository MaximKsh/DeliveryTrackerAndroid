package com.kvteam.deliverytracker.performerapp.tasks

import com.kvteam.deliverytracker.core.common.ILocalizationManager
import com.kvteam.deliverytracker.core.storage.IStorage
import com.kvteam.deliverytracker.core.webservice.IWebservice

class TaskRepository(
        private val webservice: IWebservice,
        private val storage: IStorage,
        private val localizationManager: ILocalizationManager)
    : ITaskRepository {


}