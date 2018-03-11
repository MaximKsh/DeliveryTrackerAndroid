package com.kvteam.deliverytracker.managerapp.ui.main

import android.support.v4.app.FragmentManager
import com.kvteam.deliverytracker.core.roles.Role
import com.kvteam.deliverytracker.core.ui.settings.BaseSettingsFragment
import com.kvteam.deliverytracker.managerapp.ui.main.adduser.AddUserFragment
import com.kvteam.deliverytracker.managerapp.ui.main.referenceslist.ReferenceListFragment
import com.kvteam.deliverytracker.managerapp.ui.main.settings.EditSettingsFragment
import com.kvteam.deliverytracker.managerapp.ui.main.settings.SettingsFragment
import com.kvteam.deliverytracker.managerapp.ui.main.taskdetails.SelectPerformerFragment
import com.kvteam.deliverytracker.managerapp.ui.main.taskdetails.TaskDetailsFragment
import com.kvteam.deliverytracker.managerapp.ui.main.taskdetails.TaskDetailsFragmentMode
import com.kvteam.deliverytracker.managerapp.ui.main.userslist.UsersListFragment
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class NavigationController (private val mainActivity: MainActivity) {
    private val containerId: Int
        get() = mainActivity.mainContainer.id
    private val fragmentManager: FragmentManager
        get() = mainActivity.supportFragmentManager

    val info = mutableMapOf<String, Any>()

    fun closeCurrentFragment() {
        fragmentManager.popBackStack()
    }

    fun navigateToAddPaymentReference() {
        val fragment = UsersListFragment()
        fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        fragmentManager.beginTransaction()
                .replace(containerId, fragment)
                .commitAllowingStateLoss()
    }

    fun navigateToStaff() {
        val fragment = UsersListFragment()
        fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        fragmentManager.beginTransaction()
                .replace(containerId, fragment)
                .commitAllowingStateLoss()
    }

    fun navigateToTasks() {
        val fragment = UsersListFragment()
        fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        fragmentManager.beginTransaction()
                .replace(containerId, fragment)
                .commitAllowingStateLoss()
    }

    fun navigateToAddUser(role: Role) {
        val fragment = AddUserFragment.create(role)
        fragmentManager.beginTransaction()
                .replace(containerId, fragment)
                .addToBackStack(null)
                .commitAllowingStateLoss()
    }

    fun navigateToSettings() {
        val fragment = SettingsFragment()
        fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        fragmentManager.beginTransaction()
                .replace(containerId, fragment)
                .commitAllowingStateLoss()
    }

    fun navigateToEditSettings() {
        val fragment = EditSettingsFragment()
        fragmentManager.beginTransaction()
                .replace(containerId, fragment)
                .addToBackStack(null)
                .commitAllowingStateLoss()
    }

    fun navigateToCatalogs() {
        val fragment = ReferenceListFragment()
        fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        fragmentManager.beginTransaction()
                .replace(containerId, fragment)
                .commitAllowingStateLoss()
    }

    fun navigateToAddTask() {
        val fragment = TaskDetailsFragment.create(TaskDetailsFragmentMode.ADD)
        fragmentManager.beginTransaction()
                .replace(containerId, fragment)
                .addToBackStack(null)
                .commitAllowingStateLoss()
    }

    fun navigateToSelectPerformer() {
        val fragment = SelectPerformerFragment()
        fragmentManager.beginTransaction()
                .replace(containerId, fragment)
                .addToBackStack(null)
                .commitAllowingStateLoss()
    }

    fun navigateToTask(taskId: UUID) {
        val fragment = TaskDetailsFragment.create(TaskDetailsFragmentMode.READONLY, taskId)
        fragmentManager.beginTransaction()
                .replace(containerId, fragment)
                .addToBackStack(null)
                .commitAllowingStateLoss()
    }



}