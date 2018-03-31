package com.kvteam.deliverytracker.core.ui.toolbar

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.kvteam.deliverytracker.core.async.launchUI
import com.kvteam.deliverytracker.core.common.EMPTY_STRING
import com.kvteam.deliverytracker.core.ui.DeliveryTrackerActivity
import kotlinx.android.synthetic.main.toolbar.*


class ToolbarController(
        private val activity: DeliveryTrackerActivity,
        private val toolbarConfiguration: ToolbarConfiguration) {

    val dropDownTop = DropdownTop(arrayListOf(), activity)

    var isBackButtonEnabled: Boolean = false
        private set

    var isSearchEnabled: Boolean = false
        private set

    var isSearchFragmentWide: Boolean = false
        private set

    private var dropdownOverlappedBySearch = false

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
        dropdownOverlappedBySearch = false
        activity.toolbar_title.isClickable = true
        _dropdownEnabled = true
    }

    fun disableDropDown() {
        if(toolbarConfiguration.useDropdown) {
            dropDownTop.disable()
        }
        dropdownOverlappedBySearch = false
        activity.toolbar_title.isClickable = false
        _dropdownEnabled = false
    }

    var tw: TextWatcher? = null

    fun enableSearchMode (callback: suspend (String) -> Unit,
                          callbackDelay:Long = 100,
                          fragmentWide: Boolean = false,
                          focus: Boolean = false) {
        val ddEnabled = isDropdownEnabled
        if (isDropdownEnabled) {
            disableDropDown()
        }
        dropdownOverlappedBySearch = ddEnabled

        isSearchEnabled = true
        isSearchFragmentWide = fragmentWide
        activity.updateHomeUpButton()
        activity.updateMenuItems()
        activity.toolbar_title.visibility = View.GONE
        activity.rlToolbarSearch.visibility = View.VISIBLE

        tw = object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            private var lastTime = java.lang.System.currentTimeMillis()
            override fun afterTextChanged(p0: Editable?) = launchUI {
                val currentTime = java.lang.System.currentTimeMillis()
                val diff = currentTime - lastTime
                Log.e("AAAAAA", diff.toString())
                if (diff > callbackDelay) {
                    callback(p0.toString())
                }
                lastTime = currentTime
            }
        }
        activity.etToolbarSearch.addTextChangedListener(tw)

        if(focus) {
            activity.rlToolbarSearch.requestFocus()
            val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
        }
    }

    fun disableSearchMode () {
        if(dropdownOverlappedBySearch) {
            enableDropdown()
            dropdownOverlappedBySearch = false
        }

        isSearchEnabled = false
        isSearchFragmentWide = false
        activity.updateHomeUpButton()
        activity.updateMenuItems()

        if(tw != null) {
            activity.etToolbarSearch.removeTextChangedListener(tw)
            tw = null
        }

        activity.toolbar_title.visibility = View.VISIBLE
        activity.rlToolbarSearch.visibility = View.GONE
        activity.etToolbarSearch.setText(EMPTY_STRING)

        val view =  activity.currentFocus
        if (view != null) {
            val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }
}