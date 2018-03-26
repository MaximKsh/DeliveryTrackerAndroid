package com.kvteam.deliverytracker.core.notifications

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.support.v4.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.kvteam.deliverytracker.core.DeliveryTrackerApplication
import com.kvteam.deliverytracker.core.R
import com.kvteam.deliverytracker.core.common.EMPTY_STRING
import com.kvteam.deliverytracker.core.common.ILocalizationManager
import dagger.android.AndroidInjection
import javax.inject.Inject

class DeliveryTrackerFirebaseMessageService : FirebaseMessagingService() {
    @Inject
    lateinit var lm: ILocalizationManager

    private val notificationChannel = "DefaultNotificationChannel"

    override fun onCreate() {
        AndroidInjection.inject(this)
        super.onCreate()
    }

    @SuppressLint("NewApi")
    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        if(remoteMessage == null) {
            return
        }
        val app = application as? DeliveryTrackerApplication ?: return

        val notificationService =
                getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager ?: return

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                    notificationChannel,
                    notificationChannel,
                    NotificationManager.IMPORTANCE_DEFAULT)
            notificationService
                    .createNotificationChannel(channel)
        }

        val title = remoteMessage.data?.get("title") ?: EMPTY_STRING
        val message = remoteMessage.data?.get("message") ?: EMPTY_STRING
        val action = remoteMessage.data?.get("action") ?: EMPTY_STRING
        val dataType = remoteMessage.data?.get("dataType") ?: EMPTY_STRING
        val dataSerialized = remoteMessage.data?.get("data") ?: EMPTY_STRING

        val builder = NotificationCompat.Builder(this, notificationChannel)
        builder.setSmallIcon(R.drawable.ic_tasks_black_24dp)
                .setContentTitle(lm.getString(title))
                .setContentText(lm.getString(message))
                .setAutoCancel(true)

        val contentIntent = Intent(app, app.mainActivityType as Class<*>)
        contentIntent.action = PUSH_ACTION
        contentIntent.putExtra(PUSH_ACTION_KEY, action)
        contentIntent.putExtra(PUSH_DATA_TYPE_KEY, dataType)
        contentIntent.putExtra(PUSH_DATA_SERIALIZED, dataSerialized)
        contentIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        val flags = PendingIntent.FLAG_CANCEL_CURRENT // отменить старый и создать новый
        val pendingIntent = PendingIntent.getActivity(this, PUSH_REQUEST_CODE, contentIntent, flags)
        builder.setContentIntent(pendingIntent)
        notificationService.notify(PUSH_NOTIFICATION_ID, builder.build())
    }
}