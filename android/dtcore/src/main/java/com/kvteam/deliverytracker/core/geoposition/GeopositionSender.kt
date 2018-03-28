package com.kvteam.deliverytracker.core.geoposition

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Criteria
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.util.Log
import com.kvteam.deliverytracker.core.async.launchUI
import com.kvteam.deliverytracker.core.models.Geoposition
import com.kvteam.deliverytracker.core.webservice.IWebservice
import dagger.android.AndroidInjection
import javax.inject.Inject

class GeopositionSender : BroadcastReceiver() {
    @Inject
    lateinit var webservice: IWebservice

    override fun onReceive(context: Context, int: Intent) {
        AndroidInjection.inject(this, context)
        if(ContextCompat.checkSelfPermission( context, android.Manifest.permission.ACCESS_FINE_LOCATION ) == PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission( context, android.Manifest.permission.ACCESS_COARSE_LOCATION ) == PackageManager.PERMISSION_GRANTED) {
            val ls = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            val criteria = Criteria()
            criteria.powerRequirement = Criteria.POWER_LOW
            val bestProvider = ls.getBestProvider(criteria, true)
            ls.requestSingleUpdate(bestProvider, object: LocationListener {
                override fun onLocationChanged(location: Location?) = launchUI {
                    if(location != null){
                        try {
                            val pos = Geoposition()
                            pos.latitude = location.latitude
                            pos.longitude = location.longitude
                            webservice.postAsync(
                                    "/api/performer/update_position",
                                    pos,
                                    true)
                        } catch (ex: Exception) {
                            Log.e("Geoposition", ex.message, ex)
                        }
                    }
                }

                override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
                }

                override fun onProviderEnabled(provider: String?) {
                }

                override fun onProviderDisabled(provider: String?) {
                }
            }, null)
        }
    }
}
