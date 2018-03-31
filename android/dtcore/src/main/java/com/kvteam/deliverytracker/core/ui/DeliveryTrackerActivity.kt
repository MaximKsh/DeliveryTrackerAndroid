package com.kvteam.deliverytracker.core.ui

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.FragmentManager
import com.kvteam.deliverytracker.core.DeliveryTrackerApplication
import com.kvteam.deliverytracker.core.async.launchUI
import com.kvteam.deliverytracker.core.session.CheckSessionResult
import com.kvteam.deliverytracker.core.session.ISession
import com.kvteam.deliverytracker.core.session.SETTINGS_CONTEXT
import com.kvteam.deliverytracker.core.ui.toolbar.ToolbarConfiguration
import com.kvteam.deliverytracker.core.ui.toolbar.ToolbarController
import dagger.android.AndroidInjection
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.coroutines.experimental.async
import javax.inject.Inject

abstract class DeliveryTrackerActivity : DaggerAppCompatActivity(), FragmentManager.OnBackStackChangedListener {
    @Inject
    protected lateinit var dtSession: ISession

    protected abstract val layoutId: Int

    protected open val checkHasAccountOnResume
        get() = false

    protected open val allowSettingsContext
        get() = true

    open val fabButton: FloatingActionButton?
        get() = null

    lateinit var toolbarController: ToolbarController
        private set

    protected open fun getToolbarConfiguration(): ToolbarConfiguration {
        return ToolbarConfiguration()
    }

    protected open fun configureToolbar(toolbar: ToolbarController) {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)

        if(!allowSettingsContext
                && intent.getBooleanExtra(SETTINGS_CONTEXT, false)) {
            finishAffinity()
        }

        setContentView(layoutId)

        val toolbarConfiguration = getToolbarConfiguration()
        if (toolbarConfiguration.useToolbar) {
            toolbarController = ToolbarController(this, toolbarConfiguration)
            setSupportActionBar(toolbar_top)
            supportActionBar!!.setDisplayShowTitleEnabled(false)
            toolbarController.init()
            configureToolbar(toolbarController)

            supportFragmentManager.addOnBackStackChangedListener(this)
            updateHomeUpButton()
        }
    }


    override fun onResume() = launchUI {
        super.onResume()

        if (checkHasAccountOnResume) {
            if(!dtSession.hasAccount()) {
                val intent = Intent(
                        this@DeliveryTrackerActivity,
                        (application as DeliveryTrackerApplication).loginActivityType as Class<*>)
                startActivity(intent)
                finish()
            } else {
                val result = async { dtSession.checkSessionAsync() }.await()

                if(result == CheckSessionResult.Incorrect) {
                    dtSession.logoutAsync()
                    val intent = Intent(
                            this@DeliveryTrackerActivity,
                            (application as DeliveryTrackerApplication).loginActivityType as Class<*>)
                    startActivity(intent)
                    finish()
                }
            }
        }
    }

    override fun onBackStackChanged() {
        updateHomeUpButton()
    }

    override fun onSupportNavigateUp(): Boolean {
        if (toolbarController.isSearchEnabled) {
            toolbarController.disableSearchMode()
            return true
        }
        supportFragmentManager.popBackStack()
        return true
    }

    fun updateMenuItems() {
        supportFragmentManager.fragments.forEach {
            (it as? DeliveryTrackerFragment)?.refreshMenuItems()
        }
    }

    fun updateHomeUpButton() {
        val showBack = supportFragmentManager.backStackEntryCount > 0
            || toolbarController.isSearchEnabled
        if(showBack) {
            toolbarController.showBackButton()
        } else {
            toolbarController.hideBackButton()
        }
    }
}