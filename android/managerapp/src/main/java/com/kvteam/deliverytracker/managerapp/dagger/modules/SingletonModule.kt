package com.kvteam.deliverytracker.managerapp.dagger.modules

import com.kvteam.deliverytracker.core.dagger.modules.SingletonCoreModule
import com.kvteam.deliverytracker.core.session.ISessionInfo
import com.kvteam.deliverytracker.core.storage.IStorage
import com.kvteam.deliverytracker.core.webservice.IWebservice
import com.kvteam.deliverytracker.managerapp.ManagerApplication
import com.kvteam.deliverytracker.managerapp.session.SessionInfo
import com.kvteam.deliverytracker.managerapp.tasks.ITaskRepository
import com.kvteam.deliverytracker.managerapp.tasks.TaskRepository
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class SingletonModule : SingletonCoreModule<ManagerApplication>() {
    @Provides
    @Singleton
    fun sessionInfo(): ISessionInfo {
        return SessionInfo()
    }

    @Provides
    @Singleton
    fun taskRepository(webservice: IWebservice, storage: IStorage): ITaskRepository {
        return TaskRepository(webservice, storage)
    }
}