package com.kvteam.deliverytracker.managerapp.ui.main

import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.design.widget.FloatingActionButton
import com.kvteam.deliverytracker.core.common.EMPTY_STRING
import com.kvteam.deliverytracker.core.common.IDeliveryTrackerGsonProvider
import com.kvteam.deliverytracker.core.models.TaskInfo
import com.kvteam.deliverytracker.core.notifications.*
import com.kvteam.deliverytracker.core.ui.DeliveryTrackerActivity
import com.kvteam.deliverytracker.core.ui.removeShiftMode
import com.kvteam.deliverytracker.core.ui.toolbar.ToolbarConfiguration
import com.kvteam.deliverytracker.managerapp.R
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject
import android.content.Intent
import android.support.v4.content.LocalBroadcastManager
import android.view.Window.ID_ANDROID_CONTENT
import android.opengl.ETC1.getHeight
import android.view.View
import android.view.ViewTreeObserver
import android.view.Window
import android.view.ViewGroup
import java.util.*
import android.os.CountDownTimer




class MainActivity : DeliveryTrackerActivity() {
    private val bnvSelectedItemKey = "bnvSelectedItem"

    @Inject
    lateinit var gsonProvider: IDeliveryTrackerGsonProvider

    @Inject
    lateinit var navigationController: NavigationController

    override val fabButton: FloatingActionButton?
        get() = fabAddButton

    override val layoutId = R.layout.activity_main

    private val keyboardLayoutListener = ViewTreeObserver.OnGlobalLayoutListener {
        val heightDiff = rlMainActivityContainer.rootView.height - rlMainActivityContainer.height
        val contentViewTop = window.findViewById<View>(Window.ID_ANDROID_CONTENT).height

        val broadcastManager = LocalBroadcastManager.getInstance(this@MainActivity)

        if (heightDiff <= contentViewTop) {
            onHideKeyboard()

            val intent = Intent("KeyboardWillHide")
            broadcastManager.sendBroadcast(intent)
        } else {
            val keyboardHeight = heightDiff - contentViewTop
            onShowKeyboard(keyboardHeight)

            val intent = Intent("KeyboardWillShow")
            intent.putExtra("KeyboardHeight", keyboardHeight)
            broadcastManager.sendBroadcast(intent)
        }
    }

    private var keyboardListenersAttached = false

    private fun attachKeyboardListeners() {
        if (keyboardListenersAttached) {
            return
        }

        rlMainActivityContainer.viewTreeObserver.addOnGlobalLayoutListener(keyboardLayoutListener)

        keyboardListenersAttached = true
    }

    override fun onDestroy() {
        super.onDestroy()

        if (keyboardListenersAttached) {
            rlMainActivityContainer.viewTreeObserver.removeOnGlobalLayoutListener(keyboardLayoutListener)
        }
    }

    private fun onShowKeyboard(keyboardHeight: Int) {
        navigation.visibility = View.GONE
    }

    private fun onHideKeyboard() {
        object : CountDownTimer(100, 100) {
            override fun onTick(millisUntilFinished: Long) {}

            override fun onFinish() {
                navigation.visibility = View.VISIBLE
            }
        }.start()
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

        attachKeyboardListeners()

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
