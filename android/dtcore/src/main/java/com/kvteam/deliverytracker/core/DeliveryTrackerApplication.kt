package com.kvteam.deliverytracker.core

import android.content.Intent
import com.google.firebase.FirebaseApp
import com.kvteam.deliverytracker.core.session.SessionService
import dagger.android.DaggerApplication
import java.lang.reflect.Type

abstract class DeliveryTrackerApplication : DaggerApplication() {
    abstract val loginActivityType: Type

    abstract val mainActivityType: Type

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)/*
        if (LeakCanary.isInAnalyzerProcess(this)) {
            return
        }
        LeakCanary.install(this)*/

        val intent = Intent(this, SessionService::class.java)
        startService(intent)
    }
}
