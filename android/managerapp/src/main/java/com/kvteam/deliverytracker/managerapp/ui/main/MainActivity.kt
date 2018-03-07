package com.kvteam.deliverytracker.managerapp.ui.main

import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import com.kvteam.deliverytracker.core.ui.DeliveryTrackerActivity
import com.kvteam.deliverytracker.core.ui.removeShiftMode
import com.kvteam.deliverytracker.managerapp.R
import com.kvteam.deliverytracker.core.ui.dropdowntop.DropdownTop
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.toolbar.*
import javax.inject.Inject


class MainActivity : DeliveryTrackerActivity() {
    private val bnvSelectedItemKey = "bnvSelectedItem"

    @Inject
    lateinit var navigationController: NavigationController

    override val useDropdownTop: Boolean
        get() = true

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        if(navigation.selectedItemId == item.itemId
            && supportFragmentManager.backStackEntryCount == 0) {
            return@OnNavigationItemSelectedListener true
        }

        when (item.itemId) {
            R.id.navigation_staff -> {
                navigationController.navigateToStaff()
                true
            }
            R.id.navigation_tasks -> {
                navigationController.navigateToTasks()
                true
            }
            R.id.navigation_catalogs -> {
                navigationController.navigateToCatalogs()
                true
            }
            R.id.navigation_settings -> {
                navigationController.navigateToSettings()
                true
            }
            else -> false
        }
    }

    override val checkHasAccountOnResume
        get() = true

    override val allowSettingsContext: Boolean
        get() = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(this.toolbar_top)
        supportActionBar!!.setDisplayShowTitleEnabled(false)

        if (savedInstanceState == null) {
            navigationController.navigateToStaff()
            navigation.selectedItemId = R.id.navigation_staff
        } else {
            navigation.selectedItemId =
                    savedInstanceState.getInt(bnvSelectedItemKey)
        }

        removeShiftMode(navigation)

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.apply {
            putInt(bnvSelectedItemKey, navigation.selectedItemId)
        }
    }
}
