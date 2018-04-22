package com.kvteam.deliverytracker.managerapp.ui.main

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.design.widget.FloatingActionButton
import android.util.Log
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.places.AutocompleteFilter
import com.google.android.gms.location.places.Places
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.kvteam.deliverytracker.core.common.EMPTY_STRING
import com.kvteam.deliverytracker.core.common.IDeliveryTrackerGsonProvider
import com.kvteam.deliverytracker.core.common.MapsAdapter
import com.kvteam.deliverytracker.core.models.TaskInfo
import com.kvteam.deliverytracker.core.notifications.*
import com.kvteam.deliverytracker.core.ui.DeliveryTrackerActivity
import com.kvteam.deliverytracker.core.ui.removeShiftMode
import com.kvteam.deliverytracker.core.ui.toolbar.ToolbarConfiguration
import com.kvteam.deliverytracker.managerapp.R
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject


class MainActivity : DeliveryTrackerActivity(), GoogleApiClient.OnConnectionFailedListener {
    override fun onConnectionFailed(p0: ConnectionResult) {}

    private val bnvSelectedItemKey = "bnvSelectedItem"

    lateinit var mMapsAdapter: MapsAdapter

    @Inject
    lateinit var gsonProvider: IDeliveryTrackerGsonProvider

    @Inject
    lateinit var navigationController: NavigationController

    override val fabButton: FloatingActionButton?
        get() = fabAddButton

    override val layoutId = R.layout.activity_main

    override val rootView: ViewGroup?
        get() = rlMainActivityContainer

    private fun hideBottomNavigation() {
        navigation.visibility = View.GONE
    }

    private fun showBottomNavigation() {
        navigation.visibility = View.VISIBLE
    }

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

    override fun getToolbarConfiguration(): ToolbarConfiguration {
        return ToolbarConfiguration(true, true)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        toolbarController.mainContainer = mainContainer

        val googleApiClient = GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build()

        mMapsAdapter = MapsAdapter(googleApiClient)


        if(intent.action == PUSH_ACTION) {
            val action = intent.extras[PUSH_ACTION_KEY] as? String ?: EMPTY_STRING
            val dataType = intent.extras[PUSH_DATA_TYPE_KEY] as? String ?: EMPTY_STRING
            val dataSerialized = intent.extras[PUSH_DATA_SERIALIZED] as? String ?: EMPTY_STRING
            if(action.isNotBlank()
                    && dataType.isNotBlank()
                    && dataSerialized.isNotBlank()) {
                onPushClicked(action, dataType, dataSerialized)
            } else {
                navigationController.navigateToStaff()
                navigation.selectedItemId = R.id.navigation_staff
            }
        } else if (savedInstanceState != null) {
            navigation.selectedItemId =
                    savedInstanceState.getInt(bnvSelectedItemKey)
        } else {
            navigationController.navigateToStaff()
            navigation.selectedItemId = R.id.navigation_staff
        }

        removeShiftMode(navigation)

        softKeyboard.initEditTexts()
        addOnKeyboardHideListener (::showBottomNavigation)
        addOnKeyboardShowListener (::hideBottomNavigation)

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.apply {
            putInt(bnvSelectedItemKey, navigation.selectedItemId)
        }
    }

    private fun onPushClicked(action: String,
                              dataType: String,
                              dataSerialized: String) {
        val gson = gsonProvider.gson
        when(action) {
            PUSH_OPEN_TASK -> {
                val tInfo = gson.fromJson<TaskInfo>(dataSerialized, TaskInfo::class.java)
                navigationController.navigateToTaskDetails(tInfo.id!!)
            }
        }
    }
}
