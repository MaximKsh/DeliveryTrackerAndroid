package com.kvteam.deliverytracker.performerapp.ui.main

import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import com.kvteam.deliverytracker.core.ui.DeliveryTrackerActivity
import com.kvteam.deliverytracker.core.ui.removeShiftMode
import com.kvteam.deliverytracker.performerapp.R
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject


class MainActivity : DeliveryTrackerActivity() {
    private val managersMenu = R.id.navigation_managers
    private val performersMenu = R.id.navigation_performers
    private val myTasksMenu = R.id.navigation_my_tasks
    private val undistributedTasksMenu = R.id.navigation_undistributed_tasks
    private val bnvSelectedItemKey = "bnvSelectedItem"

    private val accessPermissionsRequestCode = 1234

    private val defaultItem = myTasksMenu
    private val menuItemMapper = mapOf(
            managersMenu to {navigationController.navigateToManagers()},
            performersMenu to {navigationController.navigateToPerformers()},
            myTasksMenu to {navigationController.navigateToMyTasks()},
            undistributedTasksMenu to {navigationController.navigateToUndistributedTasks()}
    )

    @Inject
    lateinit var navigationController: NavigationController

    override val checkHasAccountOnResume = true

    override val allowSettingsContext: Boolean = false

    override val layoutId = R.layout.activity_main

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        removeShiftMode(bnvNavigation)

        if (savedInstanceState == null) {
            menuItemMapper[defaultItem]?.invoke()
            bnvNavigation.selectedItemId = defaultItem
        } else {
            bnvNavigation.selectedItemId =
                    savedInstanceState.getInt(bnvSelectedItemKey, defaultItem)
        }


        bnvNavigation.setOnNavigationItemSelectedListener {
            if(bnvNavigation.selectedItemId != it.itemId) {
                menuItemMapper[it.itemId]?.invoke()
            }
            true
        }

        if(ContextCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_COARSE_LOCATION ) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION,
                            android.Manifest.permission.ACCESS_COARSE_LOCATION),
                    accessPermissionsRequestCode)
        }
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.apply {
            putInt(bnvSelectedItemKey, bnvNavigation.selectedItemId)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {

    }

    companion object {
        @JvmStatic
        val showTaskAction = "showTaskAction"
        @JvmStatic
        val showTaskActionId = "showTaskActionId"
    }
}
