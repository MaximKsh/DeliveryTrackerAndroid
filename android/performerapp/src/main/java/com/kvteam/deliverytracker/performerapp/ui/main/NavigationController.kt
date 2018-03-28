package com.kvteam.deliverytracker.performerapp.ui.main

import android.support.v4.app.FragmentManager
import com.kvteam.deliverytracker.core.ui.FragmentTracer
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

    fun closeCurrentFragment() {
        fragmentManager.popBackStack()
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
                .replace(containerId, fragment)
                .addToBackStack(null)
                .commitAllowingStateLoss()
    }

    fun navigateToTaskDetails(id: UUID) {
        val taskDetailsFragment = TaskDetailsFragment()
        fragmentTracer.next(taskDetailsFragment)
        taskDetailsFragment.setTask(id)
        fragmentManager.beginTransaction()
                .replace(containerId, taskDetailsFragment)
                .addToBackStack(null)
                .commitAllowingStateLoss()
    }


}