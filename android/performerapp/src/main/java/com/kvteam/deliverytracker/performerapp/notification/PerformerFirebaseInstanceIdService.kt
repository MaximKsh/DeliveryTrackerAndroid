package com.kvteam.deliverytracker.performerapp.notification

import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.FirebaseInstanceIdService
import com.kvteam.deliverytracker.core.async.invokeAsync
import com.kvteam.deliverytracker.core.models.DeviceModel
import com.kvteam.deliverytracker.core.webservice.IWebservice
import dagger.android.AndroidInjection
import javax.inject.Inject

class PerformerFirebaseInstanceIdService : FirebaseInstanceIdService() {
    @Inject
    lateinit var webservice: IWebservice

    override fun onCreate() {
        AndroidInjection.inject(this)
        super.onCreate()
    }

    override fun onTokenRefresh() {
        val refreshedToken = FirebaseInstanceId.getInstance().token
        if(refreshedToken != null) {
            invokeAsync({
                webservice.post(
                    "/api/session/update_device",
                    DeviceModel(refreshedToken))
            })
        }
    }
}
