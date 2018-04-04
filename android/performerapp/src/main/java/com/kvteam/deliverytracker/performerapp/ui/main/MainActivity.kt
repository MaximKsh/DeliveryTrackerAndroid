package com.kvteam.deliverytracker.performerapp.ui.main

import android.os.Bundle
import com.kvteam.deliverytracker.core.common.EMPTY_STRING
import com.kvteam.deliverytracker.core.common.IDeliveryTrackerGsonProvider
import com.kvteam.deliverytracker.core.models.TaskInfo
import com.kvteam.deliverytracker.core.notifications.*
import com.kvteam.deliverytracker.core.ui.DeliveryTrackerActivity
import com.kvteam.deliverytracker.core.ui.removeShiftMode
import com.kvteam.deliverytracker.core.ui.toolbar.ToolbarConfiguration
import com.kvteam.deliverytracker.performerapp.R
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject


class MainActivity : DeliveryTrackerActivity() {
    private val bnvSelectedItemKey = "bnvSelectedItem"

    private val defaultItem = R.id.navigation_staff
    private val menuItemMapper = mapOf(
            R.id.navigation_staff to {navigationController.navigateToStaff()},
            R.id.navigation_tasks to {navigationController.navigateToTasks()},
            R.id.navigation_settings to {navigationController.navigateToSettings()}
    )


    @Inject
    lateinit var gsonProvider: IDeliveryTrackerGsonProvider

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

        if(intent.action == PUSH_ACTION) {
            val action = intent.extras[PUSH_ACTION_KEY] as? String ?: EMPTY_STRING
            val dataType = intent.extras[PUSH_DATA_TYPE_KEY] as? String ?: EMPTY_STRING
            val dataSerialized = intent.extras[PUSH_DATA_SERIALIZED] as? String ?: EMPTY_STRING
            if(action.isNotBlank()
                    && dataType.isNotBlank()
                    && dataSerialized.isNotBlank()) {
                onPushClicked(action, dataType, dataSerialized)
            } else {
                menuItemMapper[defaultItem]?.invoke()
                navigation.selectedItemId = defaultItem
            }
        } else if (savedInstanceState != null) {
            navigation.selectedItemId =
                    savedInstanceState.getInt(bnvSelectedItemKey, defaultItem)
        } else {
            menuItemMapper[defaultItem]?.invoke()
            navigation.selectedItemId = defaultItem
        }

        navigation.setOnNavigationItemSelectedListener {
            if(navigation.selectedItemId != it.itemId) {
                menuItemMapper[it.itemId]?.invoke()
            }
            true
        }
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.apply {
            putInt(bnvSelectedItemKey, navigation.selectedItemId)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {

    }

    private fun onPushClicked(action: String,
                              dataType: String,
                              dataSerialized: String) {
        val gson = gsonProvider.gson
        when(action) {
            PUSH_OPEN_TASK -> {
                val tInfo = gson.fromJson<TaskInfo>(dataSerialized, TaskInfo::class.java)
                menuItemMapper[R.id.navigation_tasks]?.invoke()
                navigation.selectedItemId = R.id.navigation_tasks
                navigationController.navigateToTaskDetails(tInfo.id!!)
            }
        }
    }
}
