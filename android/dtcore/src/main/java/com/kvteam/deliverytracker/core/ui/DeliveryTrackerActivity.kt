package com.kvteam.deliverytracker.core.ui

import android.content.Intent
import android.os.Bundle
import com.kvteam.deliverytracker.core.DeliveryTrackerApplication
import com.kvteam.deliverytracker.core.session.ISession
import com.kvteam.deliverytracker.core.session.SETTINGS_CONTEXT
import dagger.android.AndroidInjection
import dagger.android.support.DaggerAppCompatActivity
import javax.inject.Inject

abstract class DeliveryTrackerActivity : DaggerAppCompatActivity() {
    @Inject
    protected lateinit var dtSession: ISession

    protected open val checkHasAccountOnResume
        get() = false

    protected open val allowSettingsContext
        get() = true

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)

        if(!allowSettingsContext
                && intent.getBooleanExtra(SETTINGS_CONTEXT, false)) {
            finishAffinity()
        }
    }

    override fun onResume() {
        super.onResume()
        if (checkHasAccountOnResume
                && !dtSession.hasAccount()) {
            val intent = Intent(
                    this,
                    (application as DeliveryTrackerApplication).loginActivityType as Class<*>)
            startActivity(intent)
            finish()
        }
    }
}