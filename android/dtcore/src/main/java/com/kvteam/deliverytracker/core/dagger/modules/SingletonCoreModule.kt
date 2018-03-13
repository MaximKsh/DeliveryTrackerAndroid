package com.kvteam.deliverytracker.core.dagger.modules

import com.kvteam.deliverytracker.core.DeliveryTrackerApplication
import com.kvteam.deliverytracker.core.common.*
import com.kvteam.deliverytracker.core.session.ISession
import com.kvteam.deliverytracker.core.session.ISessionInfo
import com.kvteam.deliverytracker.core.session.Session
import com.kvteam.deliverytracker.core.storage.IStorage
import com.kvteam.deliverytracker.core.storage.Storage
import com.kvteam.deliverytracker.core.webservice.*
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
abstract class SingletonCoreModule<in T : DeliveryTrackerApplication> {
    @Provides
    @Singleton
    fun eventEmitter(): IEventEmitter {
        return EventEmitter()
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
        return Session(
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
    fun storage(app: T): IStorage {
        return Storage(app.applicationContext)
    }

    @Provides
    @Singleton
    fun localizationManager(ext: ILocalizationManagerExtension, app: T): ILocalizationManager {
        return LocalizationManager(ext, app)
    }

    @Provides
    @Singleton
    fun instanceWebservice(
            webservice: IWebservice): IInstanceWebservice {
        return InstanceWebservice(webservice)
    }

    @Provides
    @Singleton
    fun invitationWebservice(
            webservice: IWebservice): IInvitationWebservice {
        return InvitationWebservice(webservice)
    }

    @Provides
    @Singleton
    fun userWebservice(
            webservice: IWebservice): IUserWebservice {
        return UserWebservice(webservice)
    }

    @Provides
    @Singleton
    fun referenceWebservice(
            webservice: IWebservice): IReferenceWebservice {
        return ReferenceWebservice(webservice)
    }

    @Provides
    @Singleton
    fun viewWebservice(
            webservice: IWebservice): IViewWebservice {
        return ViewWebservice(webservice)
    }

    @Provides
    @Singleton
    fun taskWebservice(
            webservice: IWebservice,
            session: ISession): ITaskWebservice {
        return TaskWebservice(webservice, session)
    }
}