package com.kvteam.deliverytracker.core.ui.toolbar

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import com.kvteam.deliverytracker.core.async.launchUI
import com.kvteam.deliverytracker.core.ui.DeliveryTrackerActivity
import kotlinx.android.synthetic.main.toolbar.*
import java.util.*


class ToolbarController(
        private val activity: DeliveryTrackerActivity,
        private val toolbarConfiguration: ToolbarConfiguration) {

    val dropDownTop = DropdownTop(arrayListOf(), activity)

    var isBackButtonEnabled: Boolean = false
        private set

    var isSearchEnabled: Boolean = false
        private set

    private var _dropdownEnabled = false
    val isDropdownEnabled
        get() = toolbarConfiguration.useDropdown && _dropdownEnabled

    fun init() {
        if(toolbarConfiguration.useDropdown) {
            dropDownTop.init()
            _dropdownEnabled = true
        }
        hideBackButton()
    }

    fun setToolbarTitle(text: String) {
        activity.toolbar_title.text = text
    }

    fun showBackButton() {
        isBackButtonEnabled = true
        activity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    fun hideBackButton() {
        isBackButtonEnabled = false
        activity.supportActionBar?.setDisplayHomeAsUpEnabled(false)
    }

    fun enableDropdown() {
        if(toolbarConfiguration.useDropdown) {
            dropDownTop.showIconCollapsed()
        }
        activity.toolbar_title.isClickable = true
        _dropdownEnabled = true
    }

    fun disableDropDown() {
        if(toolbarConfiguration.useDropdown) {
            dropDownTop.hideIcon()
        }
        activity.toolbar_title.isClickable = false
        _dropdownEnabled = false
    }

    fun enableSearchMode (callback: suspend (String) -> Unit, callbackDelay:Long = 500) {
        isSearchEnabled = true
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
                            override fun run() = launchUI {
                                callback(p0.toString())
                            }
                        },
                        callbackDelay
                )
            }
        })
    }

    fun disableSearchMode () {
        isSearchEnabled = false
        if(toolbarConfiguration.useDropdown) {
            enableDropdown()
        }
        activity.toolbar_title.visibility = View.VISIBLE
        activity.rlToolbarSearch.visibility = View.GONE
    }
}