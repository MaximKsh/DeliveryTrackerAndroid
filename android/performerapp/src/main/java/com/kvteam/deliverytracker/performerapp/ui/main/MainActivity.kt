package com.kvteam.deliverytracker.performerapp.ui.main

import android.os.Bundle
import com.kvteam.deliverytracker.core.ui.DeliveryTrackerActivity
import com.kvteam.deliverytracker.core.ui.removeShiftMode
import com.kvteam.deliverytracker.core.ui.toolbar.ToolbarConfiguration
import com.kvteam.deliverytracker.performerapp.R
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject


class MainActivity : DeliveryTrackerActivity() {
    private val bnvSelectedItemKey = "bnvSelectedItem"

    private val accessPermissionsRequestCode = 1234

    private val defaultItem = R.id.navigation_staff
    private val menuItemMapper = mapOf(
            R.id.navigation_staff to {navigationController.navigateToStaff()},
            R.id.navigation_tasks to {navigationController.navigateToTasks()},
            R.id.navigation_settings to {navigationController.navigateToSettings()}
    )

    @Inject
    lateinit var navigationController: NavigationController

    override val checkHasAccountOnResume = true

    override val allowSettingsContext: Boolean = false

    override val layoutId = R.layout.activity_main

    override fun getToolbarConfiguration(): ToolbarConfiguration {
        return ToolbarConfiguration(true, true)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        removeShiftMode(navigation)

        if (savedInstanceState == null) {
            menuItemMapper[defaultItem]?.invoke()
            navigation.selectedItemId = defaultItem
        } else {
            navigation.selectedItemId =
                    savedInstanceState.getInt(bnvSelectedItemKey, defaultItem)
        }


        navigation.setOnNavigationItemSelectedListener {
            if(navigation.selectedItemId != it.itemId) {
                menuItemMapper[it.itemId]?.invoke()
            }
            true
        }
/*
        if(ContextCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_COARSE_LOCATION ) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION,
                            android.Manifest.permission.ACCESS_COARSE_LOCATION),
                    accessPermissionsRequestCode)
        }*/
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.apply {
            putInt(bnvSelectedItemKey, navigation.selectedItemId)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {

    }
}
