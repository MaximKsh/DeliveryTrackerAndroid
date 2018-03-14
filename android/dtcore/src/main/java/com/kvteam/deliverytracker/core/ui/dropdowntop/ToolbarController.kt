package com.kvteam.deliverytracker.core.ui.dropdowntop

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import com.kvteam.deliverytracker.core.ui.DeliveryTrackerActivity
import kotlinx.android.synthetic.main.toolbar.*
import java.util.*


class ToolbarController(
        private val activity: DeliveryTrackerActivity,
        private val toolbarConfiguration: ToolbarConfiguration) {

    val dropDownTop = DropdownTop(arrayListOf(), activity)

    fun init() {
        if(toolbarConfiguration.useDropdown) {
            dropDownTop.init()
        }
        hideBackButton()
    }

    fun setToolbarTitle(text: String) {
        activity.toolbar_title.text = text
    }

    fun showBackButton() {
        activity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    fun hideBackButton() {
        activity.supportActionBar?.setDisplayHomeAsUpEnabled(false)
    }

    fun enableDropdown() {
        if(toolbarConfiguration.useDropdown) {
            dropDownTop.showIconCollapsed()
        }
        activity.toolbar_title.isClickable = true
    }

    fun disableDropDown() {
        if(toolbarConfiguration.useDropdown) {
            dropDownTop.hideIcon()
        }
        activity.toolbar_title.isClickable = false
    }

    fun enableSearchMode (callback: (String) -> Unit, callbackDelay:Long = 500) {
        activity.toolbar_title.visibility = View.GONE
        activity.rlToolbarSearch.visibility = View.VISIBLE
        activity.etToolbarSearch.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            private var timer = Timer()
            override fun afterTextChanged(p0: Editable?) {
                timer.cancel()
                timer = Timer()
                timer.schedule(
                        object : TimerTask() {
                            override fun run() {
                                callback(p0.toString())
                            }
                        },
                        callbackDelay
                )
            }
        })
    }

    fun disableSearchMode () {
        if(toolbarConfiguration.useDropdown) {
            enableDropdown()
        }
        activity.toolbar_title.visibility = View.VISIBLE
        activity.rlToolbarSearch.visibility = View.GONE
    }
}