package com.kvteam.deliverytracker.managerapp.dagger.modules

import com.kvteam.deliverytracker.core.instance.IInstanceManager
import com.kvteam.deliverytracker.core.webservice.IWebservice
import com.kvteam.deliverytracker.core.instance.InstanceManager
import com.kvteam.deliverytracker.core.session.ISession
import com.kvteam.deliverytracker.core.session.Session
import com.kvteam.deliverytracker.core.webservice.Webservice
import com.kvteam.deliverytracker.managerapp.ManagerApplication
import dagger.Module
import dagger.Provides
import javax.inject.Singleton


@Module
internal class CoreModule {
    @Provides
    @Singleton
    fun webservice(session: ISession, app: ManagerApplication): IWebservice {
        return Webservice(session, app)
    }

    @Provides
    @Singleton
    fun instanceManager(webservice: IWebservice): IInstanceManager {
        return InstanceManager(webservice)
    }

    @Provides
    @Singleton
    fun session(): ISession {
        return Session()
    }
}