package com.kvteam.deliverytracker.core.session

import com.google.firebase.iid.FirebaseInstanceIdService
import com.kvteam.deliverytracker.core.async.launchUI
import dagger.android.AndroidInjection
import javax.inject.Inject

class DeliveryTrackerInstanceIdService : FirebaseInstanceIdService() {
    @Inject
    lateinit var session: ISession

    override fun onCreate() {
        AndroidInjection.inject(this)
        super.onCreate()
    }

    override fun onTokenRefresh() = launchUI {
        session.updateDeviceAsync()
    }
}
