package com.kvteam.deliverytracker.core.ui

import android.content.Context
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.view.View
import android.view.inputmethod.InputMethodManager
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
        toolbar.disableDropDown()
        toolbar.disableSearchMode()
    }

    protected open fun configureFloatingActionButton(button: FloatingActionButton) {
        button.visibility = View.GONE
    }

    open fun refreshMenuItems() {

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configureToolbar(toolbarController)

        val fabButton = dtActivity.fabButton
        if(fabButton != null) {
            configureFloatingActionButton(dtActivity.fabButton!!)
        }
    }

    override fun onPause() {
        super.onPause()
        val view =  activity!!.currentFocus
        if (view != null) {
            val imm = activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

}