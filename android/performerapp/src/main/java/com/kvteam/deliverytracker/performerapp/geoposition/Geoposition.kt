package com.kvteam.deliverytracker.performerapp.geoposition

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import java.util.*

const val GEOPOSITION_REQUEST_CODE = 0
const val GEOPOSITION_TIME_INTERVAL = 5 * 60 * 1000L //AlarmManager.INTERVAL_FIFTEEN_MINUTES

fun startSendingGeoposition(ctx: Context) {
    if(isSendingGeoposition(ctx)) {
        return
    }
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
    }
}