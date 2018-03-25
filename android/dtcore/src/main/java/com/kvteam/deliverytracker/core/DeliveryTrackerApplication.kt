package com.kvteam.deliverytracker.core

import android.content.Intent
import com.kvteam.deliverytracker.core.session.SessionService
import com.squareup.leakcanary.LeakCanary
import dagger.android.DaggerApplication
import java.lang.reflect.Type

abstract class DeliveryTrackerApplication : DaggerApplication() {
    abstract val loginActivityType: Type

    override fun onCreate() {
        super.onCreate()

        if (LeakCanary.isInAnalyzerProcess(this)) {
            return
        }
        LeakCanary.install(this);

        val intent = Intent(this, SessionService::class.java)
        startService(intent)
    }
}
