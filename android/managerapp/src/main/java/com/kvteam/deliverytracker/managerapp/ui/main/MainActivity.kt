package com.kvteam.deliverytracker.managerapp.ui.main

import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import com.kvteam.deliverytracker.core.ui.DeliveryTrackerActivity
import com.kvteam.deliverytracker.core.ui.removeShiftMode
import com.kvteam.deliverytracker.managerapp.R
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.toolbar.*
import javax.inject.Inject


class MainActivity : DeliveryTrackerActivity() {
    private val bnvSelectedItemKey = "bnvSelectedItem"

    @Inject
    lateinit var navigationController: NavigationController

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        if(navigation.selectedItemId == item.itemId) {
            return@OnNavigationItemSelectedListener true
        }

        when (item.itemId) {
            R.id.navigation_managers -> {
                navigationController.navigateToManagers()
                true
            }
            R.id.navigation_performers -> {
                navigationController.navigateToPerformers()
                true
            }
            R.id.navigation_tasks -> {
                navigationController.navigateToAllTasks()
                true
            }
            R.id.navigation_my_tasks -> {
                navigationController.navigateToMyTasks()
                true
            }
            else -> false
        }
    }

    override val checkHasAccountOnResume
        get() = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(this.toolbar_top)
        supportActionBar!!.setDisplayShowTitleEnabled(false)

        if (savedInstanceState == null) {
            navigationController.navigateToManagers()
            navigation.selectedItemId = R.id.navigation_managers
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
