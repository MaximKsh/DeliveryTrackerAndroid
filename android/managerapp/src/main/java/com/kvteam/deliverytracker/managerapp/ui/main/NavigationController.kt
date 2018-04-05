package com.kvteam.deliverytracker.managerapp.ui.main

import android.support.v4.app.FragmentManager
import com.kvteam.deliverytracker.core.R
import com.kvteam.deliverytracker.core.roles.Role
import com.kvteam.deliverytracker.core.ui.FragmentTracer
import com.kvteam.deliverytracker.managerapp.ui.main.referenceslist.ReferenceListFragment
import com.kvteam.deliverytracker.managerapp.ui.main.referenceslist.clients.ClientDetailsFragment
import com.kvteam.deliverytracker.managerapp.ui.main.referenceslist.clients.EditClientAddressFragment
import com.kvteam.deliverytracker.managerapp.ui.main.referenceslist.clients.EditClientFragment
import com.kvteam.deliverytracker.managerapp.ui.main.referenceslist.paymenttypes.EditPaymentTypeFragment
import com.kvteam.deliverytracker.managerapp.ui.main.referenceslist.paymenttypes.PaymentTypeDetailsFragment
import com.kvteam.deliverytracker.managerapp.ui.main.referenceslist.products.EditProductFragment
import com.kvteam.deliverytracker.managerapp.ui.main.referenceslist.products.FilterProductsFragment
import com.kvteam.deliverytracker.managerapp.ui.main.referenceslist.products.ProductDetailsFragment
import com.kvteam.deliverytracker.managerapp.ui.main.referenceslist.warehouses.EditWarehouseFragment
import com.kvteam.deliverytracker.managerapp.ui.main.referenceslist.warehouses.WarehouseDetailsFragment
import com.kvteam.deliverytracker.managerapp.ui.main.settings.ChangePasswordFragment
import com.kvteam.deliverytracker.managerapp.ui.main.settings.EditSettingsFragment
import com.kvteam.deliverytracker.managerapp.ui.main.settings.SettingsFragment
import com.kvteam.deliverytracker.managerapp.ui.main.taskslist.EditTaskFragment
import com.kvteam.deliverytracker.managerapp.ui.main.taskslist.TaskDetailsFragment
import com.kvteam.deliverytracker.managerapp.ui.main.taskslist.TasksListFragment
import com.kvteam.deliverytracker.managerapp.ui.main.userslist.AddUserFragment
import com.kvteam.deliverytracker.managerapp.ui.main.userslist.FilterUsersFragment
import com.kvteam.deliverytracker.managerapp.ui.main.userslist.UsersListFragment
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class NavigationController (private val mainActivity: MainActivity) {
    private val containerId: Int
        get() = mainActivity.mainContainer.id
    private val fragmentManager: FragmentManager
        get() = mainActivity.supportFragmentManager

    val fragmentTracer = FragmentTracer()

    fun closeCurrentFragment(ifEmptyFunc: () -> Unit = {}) {
        fragmentManager.popBackStack()
        if(fragmentManager.backStackEntryCount == 0) {
            ifEmptyFunc()
        } else {
            val frag = fragmentManager.findFragmentById(containerId)
            fragmentTracer.next(frag)
        }
    }

    fun navigateToStaff() {
        val usersListFragment = UsersListFragment()
        fragmentTracer.next(usersListFragment)
        fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        fragmentManager.beginTransaction()
                .replace(containerId, usersListFragment)
                .commitAllowingStateLoss()
    }

    fun navigateToTasks() {
        val tasksListFragment = TasksListFragment()
        fragmentTracer.next(tasksListFragment)
        fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        fragmentManager.beginTransaction()
                .replace(containerId, tasksListFragment)
                .commitAllowingStateLoss()
    }

    fun navigateToCatalogs() {
        val referenceListFragment = ReferenceListFragment()
        fragmentTracer.next(referenceListFragment)
        fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        fragmentManager.beginTransaction()
                .replace(containerId, referenceListFragment)
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


    fun navigateToAddUser(role: Role) {
        val fragment = AddUserFragment.create(role)
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

    fun navigateToEditTask(taskId: UUID? = null) {
        val fragment = EditTaskFragment()
        fragment.setTask(taskId)
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

    fun navigateToEditClientAddress(clientId: UUID, addressId: UUID? = null) {
        val fragment = EditClientAddressFragment()
        fragmentTracer.next(fragment)
        fragment.setAddress(clientId, addressId)
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

    fun navigateToEditPaymentType(id: UUID? = null) {
        val editPaymentTypeFragment = EditPaymentTypeFragment()
        fragmentTracer.next(editPaymentTypeFragment)
        editPaymentTypeFragment.setPaymentType(id)
        fragmentManager.beginTransaction()
                .setCustomAnimations(
                        R.anim.enter_from_right,
                        R.anim.exit_to_left,
                        R.anim.exit_to_right,
                        R.anim.enter_from_left
                )
                .replace(containerId, editPaymentTypeFragment)
                .addToBackStack(null)
                .commitAllowingStateLoss()
    }

    fun navigateToPaymentTypeDetails(id: UUID) {
        val paymentTypeDetailsFragment = PaymentTypeDetailsFragment()
        fragmentTracer.next(paymentTypeDetailsFragment)
        paymentTypeDetailsFragment.setPaymentType(id)
        fragmentManager.beginTransaction()
                .setCustomAnimations(
                        R.anim.enter_from_right,
                        R.anim.exit_to_left,
                        R.anim.exit_to_right,
                        R.anim.enter_from_left
                )
                .replace(containerId, paymentTypeDetailsFragment)
                .addToBackStack(null)
                .commitAllowingStateLoss()
    }

    fun navigateToProductDetails(id: UUID) {
        val fragment = ProductDetailsFragment()
        fragmentTracer.next(fragment)
        fragment.setProduct(id)
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

    fun navigateToClientDetails(clientId: UUID) {
        val clientDetailsFragment = ClientDetailsFragment()
        fragmentTracer.next(clientDetailsFragment)
        clientDetailsFragment.setClient(clientId)
        fragmentManager.beginTransaction()
                .setCustomAnimations(
                        R.anim.enter_from_right,
                        R.anim.exit_to_left,
                        R.anim.exit_to_right,
                        R.anim.enter_from_left
                )
                .replace(containerId, clientDetailsFragment)
                .addToBackStack(null)
                .commitAllowingStateLoss()
    }

    fun navigateToEditClient(id: UUID? = null) {
        val editClientFragment = EditClientFragment()
        fragmentTracer.next(editClientFragment)
        editClientFragment.setClient(id)
        fragmentManager.beginTransaction()
                .setCustomAnimations(
                        R.anim.enter_from_right,
                        R.anim.exit_to_left,
                        R.anim.exit_to_right,
                        R.anim.enter_from_left
                )
                .replace(containerId, editClientFragment)
                .addToBackStack(null)
                .commitAllowingStateLoss()
    }

    fun navigateToEditProduct(id: UUID? = null) {
        val fragment = EditProductFragment()
        fragmentTracer.next(fragment)
        fragment.setProduct(id)
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

    fun navigateToFilterProducts(id: UUID) {
        val fragment = FilterProductsFragment()
        fragment.setEditTaskId(id)
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

    fun navigateToFilterUsers(taskId: UUID) {
        val fragment = FilterUsersFragment()
        fragment.setEditTaskId(taskId)
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

    fun navigateToEditWarehouse(id: UUID? = null) {
        val fragment = EditWarehouseFragment()
        fragmentTracer.next(fragment)
        fragment.setWarehouse(id)
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

    fun navigateToWarehouseDetails(id: UUID) {
        val fragment = WarehouseDetailsFragment()
        fragmentTracer.next(fragment)
        fragment.setWarehouse(id)
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
}