package com.kvteam.deliverytracker.performerapp.ui.main

import android.os.Bundle
import com.kvteam.deliverytracker.core.ui.DeliveryTrackerActivity
import com.kvteam.deliverytracker.core.ui.removeShiftMode
import com.kvteam.deliverytracker.performerapp.R
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import javax.inject.Inject


class MainActivity : DeliveryTrackerActivity() {
    private val managersMenu = R.id.navigation_managers
    private val performersMenu = R.id.navigation_performers
    private val myTasksMenu = R.id.navigation_my_tasks
    private val undistributedTasksMenu = R.id.navigation_undistributed_tasks
    private val bnvSelectedItemKey = "bnvSelectedItem"

    private val defaultItem = myTasksMenu
    private val menuItemMapper = mapOf(
            managersMenu to {navigationController.navigateToManagers()},
            performersMenu to {navigationController.navigateToPerformers()},
            myTasksMenu to {navigationController.navigateToMyTasks()},
            undistributedTasksMenu to {navigationController.navigateToUndistributedTasks()}
    )

    @Inject
    lateinit var navigationController: NavigationController

    override val checkHasAccountOnResume
            get() = true

    override val allowSettingsContext: Boolean
        get() = false

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        removeShiftMode(bnvNavigation)

        if (savedInstanceState == null) {
            menuItemMapper[defaultItem]?.invoke()
            bnvNavigation.selectedItemId = defaultItem
        } else {
            bnvNavigation.selectedItemId =
                    savedInstanceState.getInt(bnvSelectedItemKey, defaultItem)
        }

        if(intent.action == showTaskAction) {
            navigationController.navigateToTask(
                    UUID.fromString(intent.extras[showTaskActionId] as String))
        }

        bnvNavigation.setOnNavigationItemSelectedListener {
            if(bnvNavigation.selectedItemId != it.itemId) {
                menuItemMapper[it.itemId]?.invoke()
            }
            true
        }
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.apply {
            putInt(bnvSelectedItemKey, bnvNavigation.selectedItemId)
        }
    }

    companion object {
        @JvmStatic
        val showTaskAction = "showTaskAction"
        @JvmStatic
        val showTaskActionId = "showTaskActionId"
    }
}
