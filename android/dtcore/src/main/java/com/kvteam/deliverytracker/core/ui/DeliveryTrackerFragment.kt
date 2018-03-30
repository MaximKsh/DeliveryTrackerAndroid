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

    val name: String = this.javaClass.simpleName

    init {
        arguments = Bundle()
    }

    protected open fun configureToolbar(toolbar: ToolbarController) {

    }

    protected open fun configureFloatingActionButton(button: FloatingActionButton) {
        button.visibility = View.GONE
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configureToolbar(toolbarController)

        val fabButton = dtActivity.fabButton
        if(fabButton != null) {
            configureFloatingActionButton(dtActivity.fabButton!!)
        }
    }

}