package com.kvteam.deliverytracker.managerapp.dagger.modules

import com.kvteam.deliverytracker.core.common.ILocalizationManagerExtension
import com.kvteam.deliverytracker.core.dagger.modules.SingletonCoreModule
import com.kvteam.deliverytracker.core.session.ISessionInfo
import com.kvteam.deliverytracker.managerapp.ManagerApplication
import com.kvteam.deliverytracker.managerapp.common.LocalizationManagerExtension
import com.kvteam.deliverytracker.managerapp.session.SessionInfo
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
    fun localizationManagerExtension(): ILocalizationManagerExtension {
        return LocalizationManagerExtension()
    }
}