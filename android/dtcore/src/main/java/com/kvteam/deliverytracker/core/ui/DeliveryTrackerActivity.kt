package com.kvteam.deliverytracker.core.ui

import android.app.Service
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.FragmentManager
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
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

    protected open val rootView: ViewGroup?
        get() = null

    lateinit var toolbarController: ToolbarController
        private set

    lateinit var softKeyboard: SoftKeyboard

    protected open fun getToolbarConfiguration(): ToolbarConfiguration {
        return ToolbarConfiguration()
    }

    protected open fun configureToolbar(toolbar: ToolbarController) {

    }

    private val keyboardShowListeners = mutableListOf<() -> Unit>()

    private val keyboardHideListeners = mutableListOf<() -> Unit>()


    private fun triggerOnKeyboardShowListeners () {
        keyboardShowListeners.forEach { it() }
    }

    private fun triggerOnKeyboardHideListeners () {
        keyboardHideListeners.forEach { it() }
    }

    fun addOnKeyboardShowListener (cb: () -> Unit) {
        keyboardShowListeners.add(cb)
    }

    fun addOnKeyboardHideListener (cb: () -> Unit) {
        keyboardHideListeners.add(cb)
    }

    fun removeOnKeyboardHideListener (cb: () -> Unit) {
        keyboardHideListeners.remove(cb)
    }

    fun removeOnKeyboardShowListener (cb: () -> Unit) {
        keyboardShowListeners.remove(cb)
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

        val rv = rootView
        if (rv != null) {
            softKeyboard = SoftKeyboard(
                    rv,
                    getSystemService(Service.INPUT_METHOD_SERVICE) as InputMethodManager)
        } else {

            softKeyboard = SoftKeyboard(
                    window.decorView.rootView as ViewGroup,
                    getSystemService(Service.INPUT_METHOD_SERVICE) as InputMethodManager)
        }



        softKeyboard.setSoftKeyboardCallback(object: SoftKeyboard.SoftKeyboardChanged {
            override fun onSoftKeyboardHide() {
                triggerOnKeyboardHideListeners()
            }

            override fun onSoftKeyboardShow() {
                triggerOnKeyboardShowListeners()
            }

        })
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

    override fun onDestroy() {
        keyboardHideListeners.clear()
        keyboardShowListeners.clear()
        softKeyboard.unRegisterSoftKeyboardCallback()
        super.onDestroy()
    }

    override fun onBackStackChanged() {
        updateHomeUpButton()
    }

    override fun onSupportNavigateUp(): Boolean {
        if (toolbarController.isSearchEnabled) {
            if (toolbarController.isSearchFragmentWide) {
                supportFragmentManager.popBackStack()
            }
            toolbarController.disableSearchMode()
        } else {
            supportFragmentManager.popBackStack()
        }
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