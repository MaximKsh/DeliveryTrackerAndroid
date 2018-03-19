package com.kvteam.deliverytracker.managerapp.ui.main

import android.support.v4.app.FragmentManager
import com.kvteam.deliverytracker.core.models.Client
import com.kvteam.deliverytracker.core.models.ClientAddress
import com.kvteam.deliverytracker.core.models.CollectionEntityAction
import com.kvteam.deliverytracker.core.models.PaymentType
import com.kvteam.deliverytracker.core.roles.Role
import com.kvteam.deliverytracker.managerapp.ui.main.referenceslist.ReferenceListFragment
import com.kvteam.deliverytracker.managerapp.ui.main.referenceslist.clients.ClientDetailsFragment
import com.kvteam.deliverytracker.managerapp.ui.main.referenceslist.clients.EditClientFragment
import com.kvteam.deliverytracker.managerapp.ui.main.referenceslist.clients.EditClientAddressFragment
import com.kvteam.deliverytracker.managerapp.ui.main.referenceslist.paymenttypes.AddPaymentTypeFragment
import com.kvteam.deliverytracker.managerapp.ui.main.referenceslist.paymenttypes.PaymentTypeDetailsFragment
import com.kvteam.deliverytracker.managerapp.ui.main.referenceslist.products.EditProductFragment
import com.kvteam.deliverytracker.managerapp.ui.main.referenceslist.products.FilterProductsFragment
import com.kvteam.deliverytracker.managerapp.ui.main.referenceslist.warehouses.EditWarehouseFragment
import com.kvteam.deliverytracker.managerapp.ui.main.settings.EditSettingsFragment
import com.kvteam.deliverytracker.managerapp.ui.main.settings.SettingsFragment
import com.kvteam.deliverytracker.managerapp.ui.main.taskslist.EditTaskFragment
import com.kvteam.deliverytracker.managerapp.ui.main.taskslist.TaskDetailsFragment
import com.kvteam.deliverytracker.managerapp.ui.main.taskslist.TasksListFragment
import com.kvteam.deliverytracker.managerapp.ui.main.userslist.AddUserFragment
import com.kvteam.deliverytracker.managerapp.ui.main.userslist.UsersListFragment
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class NavigationController (private val mainActivity: MainActivity) {
    private val containerId: Int
        get() = mainActivity.mainContainer.id
    private val fragmentManager: FragmentManager
        get() = mainActivity.supportFragmentManager

    fun closeCurrentFragment() {
        fragmentManager.popBackStack()
    }

    private val usersListFragment = UsersListFragment()
    fun navigateToStaff() {
        fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        fragmentManager.beginTransaction()
                .replace(containerId, usersListFragment)
                .commitAllowingStateLoss()
    }

    private val tasksListFragment = TasksListFragment()
    fun navigateToTasks() {
        fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        fragmentManager.beginTransaction()
                .replace(containerId, tasksListFragment)
                .commitAllowingStateLoss()
    }

    private val referenceListFragment = ReferenceListFragment()
    fun navigateToCatalogs() {
        fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        fragmentManager.beginTransaction()
                .replace(containerId, referenceListFragment)
                .commitAllowingStateLoss()
    }

    fun navigateToSettings() {
        val settingsFragment = SettingsFragment()
        fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        fragmentManager.beginTransaction()
                .replace(containerId, settingsFragment)
                .commitAllowingStateLoss()
    }

    fun navigateToEditSettings() {
        val fragment = EditSettingsFragment()
        fragmentManager.beginTransaction()
                .replace(containerId, fragment)
                .addToBackStack(null)
                .commitAllowingStateLoss()
    }

    fun navigateToAddUser(role: Role) {
        val fragment = AddUserFragment.create(role)
        fragmentManager.beginTransaction()
                .replace(containerId, fragment)
                .addToBackStack(null)
                .commitAllowingStateLoss()
    }

    fun navigateToTaskDetails(id: UUID) {
        val taskDetailsFragment = TaskDetailsFragment()
        taskDetailsFragment.setTaskId(id)
        fragmentManager.beginTransaction()
                .replace(containerId, taskDetailsFragment)
                .addToBackStack(null)
                .commitAllowingStateLoss()
    }

    fun navigateToEditTask() {
        val fragment = EditTaskFragment()
        fragmentManager.beginTransaction()
                .replace(containerId, fragment)
                .addToBackStack(null)
                .commitAllowingStateLoss()
    }

    fun navigateToEditClientAddress(type: CollectionEntityAction, address: ClientAddress? = null) {
        val fragment = EditClientAddressFragment.create(type, address)
        fragmentManager.beginTransaction()
                .replace(containerId, fragment)
                .addToBackStack(null)
                .commitAllowingStateLoss()
    }

    fun navigateToAddPaymentType(paymentType: PaymentType? = null) {
        val addPaymentTypeFragment = AddPaymentTypeFragment()
        addPaymentTypeFragment.setPaymentType(paymentType)
        fragmentManager.beginTransaction()
                .replace(containerId, addPaymentTypeFragment)
                .addToBackStack(null)
                .commitAllowingStateLoss()
    }

    fun navigateToPaymentTypeDetails(paymentType: PaymentType) {
        val paymentTypeDetailsFragment = PaymentTypeDetailsFragment()
        paymentTypeDetailsFragment.setPaymentType(paymentType)
        fragmentManager.beginTransaction()
                .replace(containerId, paymentTypeDetailsFragment)
                .addToBackStack(null)
                .commitAllowingStateLoss()
    }

    fun navigateToClientDetails(clientId: UUID) {
        val clientDetailsFragment = ClientDetailsFragment()
        clientDetailsFragment.clientId = clientId
        fragmentManager.beginTransaction()
                .replace(containerId, clientDetailsFragment)
                .addToBackStack(null)
                .commitAllowingStateLoss()
    }

    fun navigateToEditClient(client: Client? = null) {
        val editClientFragment = EditClientFragment()
        editClientFragment.setClientInfo(client)
        fragmentManager.beginTransaction()
                .replace(containerId, editClientFragment)
                .addToBackStack(null)
                .commitAllowingStateLoss()
    }

    fun navigateToEditProduct() {
        val fragment = EditProductFragment()
        fragmentManager.beginTransaction()
                .replace(containerId, fragment)
                .addToBackStack(null)
                .commitAllowingStateLoss()
    }

    fun navigateToFilterProducts() {
        val fragment = FilterProductsFragment()
        fragmentManager.beginTransaction()
                .replace(containerId, fragment)
                .addToBackStack(null)
                .commitAllowingStateLoss()
    }

    fun navigateToEditWarehouse() {
        val fragment = EditWarehouseFragment()
        fragmentManager.beginTransaction()
                .replace(containerId, fragment)
                .addToBackStack(null)
                .commitAllowingStateLoss()
    }
}