package com.kvteam.deliverytracker.core.session

import android.content.Intent
import android.os.IBinder
import com.kvteam.deliverytracker.core.DeliveryTrackerApplication
import com.kvteam.deliverytracker.core.DeliveryTrackerService
import com.kvteam.deliverytracker.core.common.IDeliveryTrackerGsonProvider
import com.kvteam.deliverytracker.core.webservice.IHttpManager
import dagger.android.AndroidInjection
import javax.inject.Inject

class SessionService: DeliveryTrackerService() {
    @Inject
    lateinit var httpManager: IHttpManager
    @Inject
    lateinit var sessionInfo: ISessionInfo
    @Inject
    lateinit var gsonProvider: IDeliveryTrackerGsonProvider

    private lateinit var accountAuthenticator: AccountAuthenticator

    override fun onCreate() {
        AndroidInjection.inject(this)
        super.onCreate()
        accountAuthenticator = AccountAuthenticator(
                gsonProvider,
                application as DeliveryTrackerApplication,
                httpManager,
                sessionInfo)
    }

    override fun onBind(intent: Intent?): IBinder {
        return accountAuthenticator.iBinder
    }
}