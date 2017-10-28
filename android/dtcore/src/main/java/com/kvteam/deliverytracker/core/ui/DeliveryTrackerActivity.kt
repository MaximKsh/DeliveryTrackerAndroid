package com.kvteam.deliverytracker.core.ui

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import com.kvteam.deliverytracker.core.DeliveryTrackerApplication
import com.kvteam.deliverytracker.core.session.ISession
import dagger.android.AndroidInjection
import dagger.android.support.DaggerAppCompatActivity
import javax.inject.Inject

abstract class DeliveryTrackerActivity : DaggerAppCompatActivity() {
    @Inject
    protected lateinit var dtSession: ISession

    protected open val checkHasAccountOnResume
        get() = false

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
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

    override fun onSaveInstanceState(outState: Bundle?, outPersistentState: PersistableBundle?) {
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
    }


}