package com.kvteam.deliverytracker.performerapp.ui.main

import android.support.v4.app.FragmentManager
import com.kvteam.deliverytracker.performerapp.ui.main.task.TaskFragment
import com.kvteam.deliverytracker.performerapp.ui.main.taskslist.MyTasksListFragment
import com.kvteam.deliverytracker.performerapp.ui.main.taskslist.UndistributedTasksListFragment
import com.kvteam.deliverytracker.performerapp.ui.main.userslist.ManagersListFragment
import com.kvteam.deliverytracker.performerapp.ui.main.userslist.PerformersListFragment
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class NavigationController (private val mainActivity: MainActivity) {
    private val containerId: Int
        get() = mainActivity.container.id
    private val fragmentManager: FragmentManager
        get() = mainActivity.supportFragmentManager

    fun closeCurrentFragment() {
        fragmentManager.popBackStack()
    }

    fun navigateToTask(taskId: UUID) {
        val fragment = TaskFragment.create(taskId)
        fragmentManager.beginTransaction()
                .replace(containerId, fragment)
                .addToBackStack(null)
                .commitAllowingStateLoss()
    }

    fun navigateToManagers() {
        val fragment = ManagersListFragment()
        fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        fragmentManager.beginTransaction()
                .replace(containerId, fragment)
                .commitAllowingStateLoss()
    }

    fun navigateToPerformers() {
        val fragment = PerformersListFragment()
        fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        fragmentManager.beginTransaction()
                .replace(containerId, fragment)
                .commitAllowingStateLoss()
    }

    fun navigateToMyTasks() {
        val fragment = MyTasksListFragment()
        fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        fragmentManager.beginTransaction()
                .replace(containerId, fragment)
                .commitAllowingStateLoss()
    }

    fun navigateToUndistributedTasks() {
        val fragment = UndistributedTasksListFragment()
        fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        fragmentManager.beginTransaction()
                .replace(containerId, fragment)
                .commitAllowingStateLoss()
    }

}