package com.kvteam.deliverytracker.core.geoposition

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.kvteam.deliverytracker.core.webservice.IWebservice
import dagger.android.AndroidInjection
import javax.inject.Inject

class GeopositionSender : BroadcastReceiver() {
    @Inject
    lateinit var webservice: IWebservice

    override fun onReceive(context: Context, int: Intent) {
        AndroidInjection.inject(this, context)
        sendGeoposition(context, webservice)
    }
}
