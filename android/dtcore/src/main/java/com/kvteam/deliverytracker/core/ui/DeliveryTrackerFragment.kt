package com.kvteam.deliverytracker.core.ui

import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.view.View
import com.kvteam.deliverytracker.core.ui.toolbar.ToolbarController

abstract class DeliveryTrackerFragment: Fragment() {
    protected val dtActivity
       get() = activity as DeliveryTrackerActivity

    protected val toolbarController
        get() = dtActivity.toolbarController

    protected val dropdownTop
        get() = toolbarController.dropDownTop

    protected var backStackEntryCount: Int = -1

    val name: String = this.javaClass.simpleName

    open val useSoftKeyboardFeatures = true

    init {
        arguments = Bundle()
    }

    protected open fun configureToolbar(toolbar: ToolbarController) {
        toolbar.disableDropDown()
        toolbar.disableSearchMode()
    }

    protected open fun configureFloatingActionButton(button: FloatingActionButton) {
        button.visibility = View.GONE
    }

    open fun refreshMenuItems() {

    }

    protected open fun onPopFragmentFromBackstack() {

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configureToolbar(toolbarController)

        val fabButton = dtActivity.fabButton
        if(fabButton != null) {
            configureFloatingActionButton(dtActivity.fabButton!!)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (useSoftKeyboardFeatures) {
            dtActivity.softKeyboard.initEditTexts()
        }
    }

    override fun onStart() {
        super.onStart()
        val currentActivity = activity as? DeliveryTrackerActivity
        if (currentActivity != null) {
            backStackEntryCount = currentActivity.supportFragmentManager.backStackEntryCount
        }
    }

    override fun onPause() {
        dtActivity.softKeyboard.closeSoftKeyboard()
        super.onPause()
    }

    override fun onDestroy() {
        val currentActivity = activity as? DeliveryTrackerActivity
        if (currentActivity != null) {
            val currentBackStackEntryCount = currentActivity.supportFragmentManager.backStackEntryCount
            if (currentBackStackEntryCount < backStackEntryCount) {
                onPopFragmentFromBackstack()
            }
        }
        super.onDestroy()
    }

    protected fun showFab() {
        val fab = (activity as? DeliveryTrackerActivity)?.fabButton
        if ( fab != null ) {
            configureFloatingActionButton(fab)
        }
    }

    protected fun hideFab() {
        val fab = (activity as? DeliveryTrackerActivity)?.fabButton
        if ( fab != null ) {
            fab.visibility = View.GONE
        }
    }

}