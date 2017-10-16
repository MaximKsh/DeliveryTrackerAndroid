package com.kvteam.deliverytracker.managerapp.dagger.modules

import com.kvteam.deliverytracker.core.webservice.IInstanceManager
import com.kvteam.deliverytracker.core.webservice.IWebservice
import com.kvteam.deliverytracker.core.webservice.InstanceManager
import com.kvteam.deliverytracker.core.webservice.Webservice
import com.kvteam.deliverytracker.managerapp.ManagerApplication
import dagger.Module
import dagger.Provides
import javax.inject.Singleton


@Module
internal class WebserviceModule{
    @Provides
    @Singleton
    fun webservice(app: ManagerApplication): IWebservice {
        return Webservice(app)
    }

    @Provides
    @Singleton
    fun instanceManager(webservice: IWebservice): IInstanceManager {
        return InstanceManager(webservice)
    }
}