package com.kvteam.deliverytracker.performerapp.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.support.v4.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.kvteam.deliverytracker.core.common.EMPTY_STRING
import com.kvteam.deliverytracker.core.common.ILocalizationManager
import com.kvteam.deliverytracker.performerapp.R
import com.kvteam.deliverytracker.performerapp.ui.main.MainActivity
import dagger.android.AndroidInjection
import javax.inject.Inject


class PerformerFirebaseMessageService : FirebaseMessagingService() {
    @Inject
    lateinit var lm: ILocalizationManager

    val notificationChannel = "DefaultNotificationChannel"
    val addTaskRequestCode = 0

    override fun onCreate() {
        AndroidInjection.inject(this)
        super.onCreate()
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        if(remoteMessage == null) {
            return
        }
        val notificationService =
                getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager ?: return

        if (Build.VERSION.SDK_INT >= 26) {
            val channel = NotificationChannel(
                    notificationChannel,
                    notificationChannel,
                    NotificationManager.IMPORTANCE_DEFAULT)
            notificationService
                    .createNotificationChannel(channel)
        }

        val builder = NotificationCompat.Builder(this, notificationChannel)
        builder.setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle(lm.getString(R.string.Core_NotificationHeader))
                .setContentText(lm.getString(remoteMessage.data?.get("msg") ?: EMPTY_STRING))
                .setAutoCancel(true)

        if(remoteMessage.data?.get("taskId") ?: EMPTY_STRING != EMPTY_STRING) {
            val contentIntent = Intent(this, MainActivity::class.java)
            contentIntent.action = MainActivity.showTaskAction
            contentIntent.putExtra(MainActivity.showTaskActionId, remoteMessage.data?.get("taskId"))
            contentIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            val flags = PendingIntent.FLAG_CANCEL_CURRENT // отменить старый и создать новый
            val pendingIntent = PendingIntent.getActivity(this, addTaskRequestCode, contentIntent, flags)
            builder.setContentIntent(pendingIntent)
        }
        notificationService.notify(1, builder.build())
    }
}
