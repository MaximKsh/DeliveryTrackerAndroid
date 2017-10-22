package com.kvteam.deliverytracker.core.session

import android.content.Intent
import android.os.IBinder
import com.kvteam.deliverytracker.core.DeliveryTrackerApplication
import com.kvteam.deliverytracker.core.DeliveryTrackerService
import com.kvteam.deliverytracker.core.webservice.IWebservice
import dagger.android.AndroidInjection
import javax.inject.Inject

class SessionService: DeliveryTrackerService() {
    @Inject
    lateinit var webservice: IWebservice
    @Inject
    lateinit var sessionInfo: ISessionInfo

    private lateinit var accountAuthenticator: AccountAuthenticator

    override fun onCreate() {
        AndroidInjection.inject(this)
        super.onCreate()
        accountAuthenticator = AccountAuthenticator(
                application as DeliveryTrackerApplication,
                webservice,
                sessionInfo)
    }

    override fun onBind(intent: Intent?): IBinder {
        return accountAuthenticator.iBinder
    }
}