package com.kvteam.deliverytracker.performerapp.dagger.modules

import com.kvteam.deliverytracker.core.common.ILocalizationManager
import com.kvteam.deliverytracker.core.common.ILocalizationManagerExtension
import com.kvteam.deliverytracker.core.dagger.modules.SingletonCoreModule
import com.kvteam.deliverytracker.core.session.ISessionInfo
import com.kvteam.deliverytracker.core.storage.IStorage
import com.kvteam.deliverytracker.core.webservice.IWebservice
import com.kvteam.deliverytracker.performerapp.PerformerApplication
import com.kvteam.deliverytracker.performerapp.common.LocalizationManagerExtension
import com.kvteam.deliverytracker.performerapp.session.SessionInfo
import com.kvteam.deliverytracker.performerapp.tasks.ITaskRepository
import com.kvteam.deliverytracker.performerapp.tasks.TaskRepository
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class SingletonModule : SingletonCoreModule<PerformerApplication>() {
    @Provides
    @Singleton
    fun sessionInfo(): ISessionInfo {
        return SessionInfo()
    }

    @Provides
    @Singleton
    fun localizationManagerExtension(): ILocalizationManagerExtension {
        return LocalizationManagerExtension()
    }

    @Provides
    @Singleton
    fun taskRepository(
            webservice: IWebservice,
            storage: IStorage,
            localizationManager: ILocalizationManager): ITaskRepository {
        return TaskRepository(webservice, storage, localizationManager)
    }
}