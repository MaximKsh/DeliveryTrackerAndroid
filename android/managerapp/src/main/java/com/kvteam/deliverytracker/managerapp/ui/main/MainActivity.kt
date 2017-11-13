package com.kvteam.deliverytracker.managerapp.ui.main

import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main.*
import android.support.design.internal.BottomNavigationItemView
import android.support.design.internal.BottomNavigationMenuView
import android.util.Log
import com.kvteam.deliverytracker.core.ui.DeliveryTrackerActivity
import com.kvteam.deliverytracker.core.ui.removeShiftMode
import com.kvteam.deliverytracker.managerapp.R
import com.kvteam.deliverytracker.managerapp.ui.main.userslist.ManagersListFragment
import kotlinx.android.synthetic.main.toolbar.*
import javax.inject.Inject


class MainActivity : DeliveryTrackerActivity() {
    @Inject
    lateinit var navigationController: NavigationController

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_managers -> {
                navigationController.navigateToManagers()
                true
            }
            R.id.navigation_performers -> {
                true
            }
            R.id.navigation_tasks -> {
                true
            }
            R.id.navigation_my_tasks -> {
                true
            }
            else -> false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(this.toolbar_top)
        supportActionBar!!.setDisplayShowTitleEnabled(false)

        val transaction = supportFragmentManager.beginTransaction()
        transaction.add(R.id.mainContainer, ManagersListFragment())
        transaction.addToBackStack(null)
        transaction.commit()

        removeShiftMode(navigation)

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
    }
}
