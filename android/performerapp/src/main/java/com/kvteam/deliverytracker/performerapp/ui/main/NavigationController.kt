package com.kvteam.deliverytracker.performerapp.ui.main

import android.support.v4.app.FragmentManager
import com.kvteam.deliverytracker.core.R
import com.kvteam.deliverytracker.core.ui.FragmentTracer
import com.kvteam.deliverytracker.performerapp.ui.main.dayroute.DayRouteFragment
import com.kvteam.deliverytracker.performerapp.ui.main.settings.ChangePasswordFragment
import com.kvteam.deliverytracker.performerapp.ui.main.settings.EditSettingsFragment
import com.kvteam.deliverytracker.performerapp.ui.main.settings.SettingsFragment
import com.kvteam.deliverytracker.performerapp.ui.main.taskslist.TaskDetailsFragment
import com.kvteam.deliverytracker.performerapp.ui.main.taskslist.TasksListFragment
import com.kvteam.deliverytracker.performerapp.ui.main.userslist.UsersListFragment
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class NavigationController (private val mainActivity: MainActivity) {
    private val containerId: Int
        get() = mainActivity.mainContainer.id
    private val fragmentManager: FragmentManager
        get() = mainActivity.supportFragmentManager


    val fragmentTracer = FragmentTracer()

    fun closeCurrentFragment(onBackStackEmpty: () -> Unit = {}) {
        fragmentManager.popBackStack()

        if (fragmentManager.backStackEntryCount == 0) {
            onBackStackEmpty()
        }
    }

    private val usersListFragment = UsersListFragment()
    fun navigateToStaff() {
        fragmentTracer.next(usersListFragment)
        fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        fragmentManager.beginTransaction()
                .replace(containerId, usersListFragment)
                .commitAllowingStateLoss()
    }

    private val tasksListFragment = TasksListFragment()
    fun navigateToTasks() {
        fragmentTracer.next(tasksListFragment)
        fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        fragmentManager.beginTransaction()
                .replace(containerId, tasksListFragment)
                .commitAllowingStateLoss()
    }

    fun navigateToDayRoute() {
        val dayRouteFragment = DayRouteFragment()
        fragmentTracer.next(dayRouteFragment)
        fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        fragmentManager.beginTransaction()
                .replace(containerId, dayRouteFragment)
                .commitAllowingStateLoss()
    }

    fun navigateToSettings() {
        val settingsFragment = SettingsFragment()
        fragmentTracer.next(settingsFragment)
        fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        fragmentManager.beginTransaction()
                .replace(containerId, settingsFragment)
                .commitAllowingStateLoss()
    }

    fun navigateToEditSettings() {
        val fragment = EditSettingsFragment()
        fragmentTracer.next(fragment)
        fragmentManager.beginTransaction()
                .setCustomAnimations(
                        R.anim.enter_from_right,
                        R.anim.exit_to_left,
                        R.anim.exit_to_right,
                        R.anim.enter_from_left
                )
                .replace(containerId, fragment)
                .addToBackStack(null)
                .commitAllowingStateLoss()
    }

    fun navigateToChangePassword() {
        val fragment = ChangePasswordFragment()
        fragmentTracer.next(fragment)
        fragmentManager.beginTransaction()
                .setCustomAnimations(
                        R.anim.enter_from_right,
                        R.anim.exit_to_left,
                        R.anim.exit_to_right,
                        R.anim.enter_from_left
                )
                .replace(containerId, fragment)
                .addToBackStack(null)
                .commitAllowingStateLoss()
    }


    fun navigateToTaskDetails(id: UUID) {
        val taskDetailsFragment = TaskDetailsFragment()
        fragmentTracer.next(taskDetailsFragment)
        taskDetailsFragment.setTask(id)
        fragmentManager.beginTransaction()
                .setCustomAnimations(
                        R.anim.enter_from_right,
                        R.anim.exit_to_left,
                        R.anim.exit_to_right,
                        R.anim.enter_from_left
                )
                .replace(containerId, taskDetailsFragment)
                .addToBackStack(null)
                .commitAllowingStateLoss()
    }


}