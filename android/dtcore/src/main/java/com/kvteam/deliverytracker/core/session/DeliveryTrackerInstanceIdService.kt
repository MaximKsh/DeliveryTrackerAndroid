package com.kvteam.deliverytracker.core.session

import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.FirebaseInstanceIdService
import com.kvteam.deliverytracker.core.async.launchUI
import com.kvteam.deliverytracker.core.webservice.IWebservice
import dagger.android.AndroidInjection
import javax.inject.Inject

class DeliveryTrackerInstanceIdService : FirebaseInstanceIdService() {
    @Inject
    lateinit var webservice: IWebservice

    override fun onCreate() {
        AndroidInjection.inject(this)
        super.onCreate()
    }

    override fun onTokenRefresh() = launchUI {
        val refreshedToken = FirebaseInstanceId.getInstance().token

        if(refreshedToken != null) {
            //webservice.postAsync("/api/session/update_device", DeviceModel())
        }
    }
}
