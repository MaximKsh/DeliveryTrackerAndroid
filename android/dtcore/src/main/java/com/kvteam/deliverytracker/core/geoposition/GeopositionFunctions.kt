package com.kvteam.deliverytracker.core.geoposition

import android.app.AlarmManager
import android.app.PendingIntent
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
import com.kvteam.deliverytracker.core.webservice.viewmodels.GeopositionRequest
import java.util.*


const val GEOPOSITION_REQUEST_CODE = 0
const val GEOPOSITION_TIME_INTERVAL = 20 * 1000L //AlarmManager.INTERVAL_FIFTEEN_MINUTES

const val GEOPOSITION_PERMISSION_REQUEST_CODE = 111

fun startSendingGeoposition(ctx: Context, webservice: IWebservice) {
    if(isSendingGeoposition(ctx)) {
        return
    }
    sendGeoposition(ctx, webservice)
    val alarmManager = ctx.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val pendingIntent = PendingIntent.getBroadcast(
            ctx,
            GEOPOSITION_REQUEST_CODE,
            Intent(ctx, GeopositionSender::class.java),
            PendingIntent.FLAG_CANCEL_CURRENT)

    alarmManager.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            Calendar.getInstance().timeInMillis,
            GEOPOSITION_TIME_INTERVAL,
            pendingIntent)
}

fun isSendingGeoposition(ctx: Context) : Boolean {
    return PendingIntent.getBroadcast(
            ctx,
            GEOPOSITION_REQUEST_CODE,
            Intent(ctx, GeopositionSender::class.java),
            PendingIntent.FLAG_NO_CREATE) != null
}

fun stopSendingGeoposition(ctx: Context) {
    val pendingIntent = PendingIntent.getBroadcast(
            ctx,
            GEOPOSITION_REQUEST_CODE,
            Intent(ctx, GeopositionSender::class.java),
            PendingIntent.FLAG_NO_CREATE)
    if(pendingIntent != null) {
        val alarmManager = ctx.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(pendingIntent)
        pendingIntent.cancel()
    }
}

fun sendGeoposition (ctx: Context, webservice: IWebservice) {
    if(ContextCompat.checkSelfPermission( ctx, android.Manifest.permission.ACCESS_FINE_LOCATION ) == PackageManager.PERMISSION_GRANTED
            || ContextCompat.checkSelfPermission( ctx, android.Manifest.permission.ACCESS_COARSE_LOCATION ) == PackageManager.PERMISSION_GRANTED) {
        val ls = ctx.getSystemService(Context.LOCATION_SERVICE) as LocationManager
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
                                "/api/geopositioning/update",
                                GeopositionRequest(geoposition = pos),
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