package com.kvteam.deliverytracker.core.dagger.modules

import android.arch.lifecycle.ViewModel
import com.kvteam.deliverytracker.core.DeliveryTrackerApplication
import com.kvteam.deliverytracker.core.ui.DeliveryTrackerViewModelFactory
import com.kvteam.deliverytracker.core.instance.IInstanceManager
import com.kvteam.deliverytracker.core.instance.InstanceManager
import com.kvteam.deliverytracker.core.session.ISession
import com.kvteam.deliverytracker.core.session.ISessionInfo
import com.kvteam.deliverytracker.core.session.Session
import com.kvteam.deliverytracker.core.storage.IStorage
import com.kvteam.deliverytracker.core.storage.Storage
import com.kvteam.deliverytracker.core.webservice.HttpManager
import com.kvteam.deliverytracker.core.webservice.IHttpManager
import com.kvteam.deliverytracker.core.webservice.IWebservice
import com.kvteam.deliverytracker.core.webservice.Webservice
import dagger.Module
import dagger.Provides
import javax.inject.Provider
import javax.inject.Singleton

@Module
abstract class SingletonCoreModule<in T : DeliveryTrackerApplication> {
    @Provides
    @Singleton
    fun viewModelFactory(
            creators: MutableMap<Class<out ViewModel>, Provider<ViewModel>>): DeliveryTrackerViewModelFactory {
        return DeliveryTrackerViewModelFactory(creators)
    }

    @Provides
    @Singleton
    fun httpManager(): IHttpManager {
        return HttpManager()
    }

    @Provides
    @Singleton
    fun session(
            httpManager: IHttpManager,
            app: T,
            sessionInfo: ISessionInfo): ISession {
        return Session(httpManager, sessionInfo, app.applicationContext)
    }

    @Provides
    @Singleton
    fun webservice(
            app: T,
            session: ISession,
            httpManager: IHttpManager): IWebservice {
        return Webservice(app.applicationContext, session, httpManager)
    }

    @Provides
    @Singleton
    fun instanceManager(webservice: IWebservice, storage: IStorage): IInstanceManager {
        return InstanceManager(webservice, storage)
    }

    @Provides
    @Singleton
    fun storage(app: T): IStorage {
        return Storage(app.applicationContext)
    }
}