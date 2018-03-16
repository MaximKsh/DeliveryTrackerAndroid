package com.kvteam.deliverytracker.core.ui

import android.os.Bundle
import android.support.v4.app.Fragment
import com.kvteam.deliverytracker.core.ui.dropdowntop.ToolbarController

abstract class DeliveryTrackerFragment: Fragment() {
    protected val dtActivity
       get() = activity as DeliveryTrackerActivity

    protected val toolbarController
        get() = dtActivity.toolbarController

    protected val dropdownTop
        get() = toolbarController.dropDownTop

    init {
        arguments = Bundle()
    }

    protected open fun configureToolbar(toolbar: ToolbarController) {

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        configureToolbar(toolbarController)
    }
}