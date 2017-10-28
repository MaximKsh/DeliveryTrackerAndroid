package com.kvteam.deliverytracker.performerapp.ui.main

import android.support.v4.app.FragmentManager
import com.kvteam.deliverytracker.performerapp.ui.main.task.TaskFragment
import com.kvteam.deliverytracker.performerapp.ui.main.userslist.ManagersListFragment
import com.kvteam.deliverytracker.performerapp.ui.main.userslist.PerformersListFragment
import kotlinx.android.synthetic.main.activity_main.*

class NavigationController (private val mainActivity: MainActivity) {
    private val containerId: Int
        get() = mainActivity.container.id
    private val fragmentManager: FragmentManager
        get() = mainActivity.supportFragmentManager

    fun navigateToTask() {
        val fragment = TaskFragment()
        fragmentManager.beginTransaction()
                .replace(containerId, fragment)
                .commitAllowingStateLoss()
    }

    fun navigateToManagers() {
        val fragment = ManagersListFragment()
        fragmentManager.beginTransaction()
                .replace(containerId, fragment)
                .commitAllowingStateLoss()
    }

    fun navigateToPerformers() {
        val fragment = PerformersListFragment()
        fragmentManager.beginTransaction()
                .replace(containerId, fragment)
                .commitAllowingStateLoss()
    }

    fun navigateToMyTasks() {
        navigateToTask()
//        val fragment = ManagersListFragment()
//        fragmentManager.beginTransaction()
//                .replace(containerId, fragment)
//                .commitAllowingStateLoss()
    }

    fun navigateToUndistributedTasks() {
        navigateToTask()
//        val fragment = ManagersListFragment()
//        fragmentManager.beginTransaction()
//                .replace(containerId, fragment)
//                .commitAllowingStateLoss()
    }

}