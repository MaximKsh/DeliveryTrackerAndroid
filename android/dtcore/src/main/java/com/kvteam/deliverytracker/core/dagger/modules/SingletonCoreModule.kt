package com.kvteam.deliverytracker.core.dagger.modules

import com.kvteam.deliverytracker.core.DeliveryTrackerApplication
import com.kvteam.deliverytracker.core.common.*
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
import javax.inject.Singleton

@Module
abstract class SingletonCoreModule<in T : DeliveryTrackerApplication> {
    @Provides
    @Singleton
    fun httpManager(): IHttpManager {
        return HttpManager()
    }

    @Provides
    @Singleton
    fun session(
            localizationManager: ILocalizationManager,
            errorManager: IErrorManager,
            httpManager: IHttpManager,
            app: T,
            sessionInfo: ISessionInfo): ISession {
        return Session(
                localizationManager,
                errorManager,
                httpManager,
                sessionInfo,
                app.applicationContext)
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
    fun instanceManager(
            webservice: IWebservice,
            storage: IStorage,
            errorManager: IErrorManager,
            localizationManager: ILocalizationManager): IInstanceManager {
        return InstanceManager(webservice, storage, errorManager, localizationManager)
    }

    @Provides
    @Singleton
    fun storage(app: T): IStorage {
        return Storage(app.applicationContext)
    }

    @Provides
    @Singleton
    fun errorManager(): IErrorManager {
        return ErrorManager()
    }

    @Provides
    @Singleton
    fun localizationManager(ext: ILocalizationManagerExtension, app: T): ILocalizationManager {
        return LocalizationManager(ext, app)
    }
}