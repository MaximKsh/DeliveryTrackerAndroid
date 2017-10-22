package com.kvteam.deliverytracker.core

import android.content.Intent
import com.kvteam.deliverytracker.core.session.ISession
import dagger.android.support.DaggerAppCompatActivity
import javax.inject.Inject

abstract class DeliveryTrackerActivity : DaggerAppCompatActivity() {
    @Inject
    lateinit var dtSession: ISession

    open val checkHasAccountOnResume
        get() = false

    override fun onResume() {
        super.onResume()
        if(checkHasAccountOnResume
                && !dtSession.hasAccount()) {
            val intent = Intent(
                    this,
                    (application as DeliveryTrackerApplication).loginActivityType as Class<*>)
            startActivity(intent)
            finish()
        }
    }

}