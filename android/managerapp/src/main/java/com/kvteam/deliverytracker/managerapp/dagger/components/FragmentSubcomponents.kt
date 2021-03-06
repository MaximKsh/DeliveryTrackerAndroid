package com.kvteam.deliverytracker.managerapp.dagger.components

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
import com.kvteam.deliverytracker.managerapp.ui.main.taskslist.*
import com.kvteam.deliverytracker.managerapp.ui.main.userslist.*
import dagger.Subcomponent
import dagger.android.AndroidInjector


@Subcomponent
interface AddUserFragmentSubcomponent : AndroidInjector<AddUserFragment> {
    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<AddUserFragment>()
}

@Subcomponent
interface AddPaymentTypeSubcomponent : AndroidInjector<EditPaymentTypeFragment> {
    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<EditPaymentTypeFragment>()
}

@Subcomponent
interface EditProductSubcomponent : AndroidInjector<EditProductFragment> {
    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<EditProductFragment>()
}

@Subcomponent
interface EditTaskSubcomponent : AndroidInjector<EditTaskFragment> {
    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<EditTaskFragment>()
}

@Subcomponent
interface TaskNumberAndDetailsSubcomponent : AndroidInjector<TaskNumberAndDetailsFragment> {
    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<TaskNumberAndDetailsFragment>()
}

@Subcomponent
interface TaskProductsSubcomponent : AndroidInjector<TaskProductsFragment> {
    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<TaskProductsFragment>()
}

@Subcomponent
interface TaskClientSubcomponent : AndroidInjector<TaskClientFragment> {
    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<TaskClientFragment>()
}

@Subcomponent
interface TaskReceiptAtSubcomponent : AndroidInjector<TaskReceiptAtFragment> {
    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<TaskReceiptAtFragment>()
}

@Subcomponent
interface TaskDeliveryDateSubcomponent : AndroidInjector<TaskDeliveryDateFragment> {
    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<TaskDeliveryDateFragment>()
}

@Subcomponent
interface EditWarehouseSubcomponent : AndroidInjector<EditWarehouseFragment> {
    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<EditWarehouseFragment>()
}

@Subcomponent
interface WarehouseDetailsSubcomponent : AndroidInjector<WarehouseDetailsFragment> {
    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<WarehouseDetailsFragment>()
}

@Subcomponent
interface ProductDetailsSubcomponent : AndroidInjector<ProductDetailsFragment> {
    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<ProductDetailsFragment>()
}

@Subcomponent
interface FilterProductsSubcomponent : AndroidInjector<FilterProductsFragment> {
    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<FilterProductsFragment>()
}

@Subcomponent
interface FilterUsersSubcomponent : AndroidInjector<FilterUsersFragment> {
    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<FilterUsersFragment>()
}

@Subcomponent
interface EditClientSubcomponent : AndroidInjector<EditClientFragment> {
    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<EditClientFragment>()
}

@Subcomponent
interface ClientDetailsSubcomponent : AndroidInjector<ClientDetailsFragment> {
    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<ClientDetailsFragment>()
}

@Subcomponent
interface PaymentTypeDetailsSubcomponent : AndroidInjector<PaymentTypeDetailsFragment> {
    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<PaymentTypeDetailsFragment>()
}

@Subcomponent
interface EditClientAddressSubcomponent : AndroidInjector<EditClientAddressFragment> {
    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<EditClientAddressFragment>()
}

@Subcomponent
interface UsersListFragmentSubcomponent : AndroidInjector<UsersListFragment> {
    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<UsersListFragment>()
}

@Subcomponent
interface UserTasksListFragmentSubcomponent : AndroidInjector<UserTasksListFragment> {
    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<UserTasksListFragment>()
}

@Subcomponent
interface TasksListFragmentSubcomponent : AndroidInjector<TasksListFragment> {
    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<TasksListFragment>()
}

@Subcomponent
interface ReferenceListFragmentSubcomponent : AndroidInjector<ReferenceListFragment> {
    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<ReferenceListFragment>()
}

@Subcomponent
interface SettingsFragmentSubcomponent : AndroidInjector<SettingsFragment> {
    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<SettingsFragment>()
}

@Subcomponent
interface EditSettingsFragmentSubcomponent : AndroidInjector<EditSettingsFragment> {
    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<EditSettingsFragment>()
}

@Subcomponent
interface ChangePasswordFragmentSubcomponent : AndroidInjector<ChangePasswordFragment> {
    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<ChangePasswordFragment>()
}

@Subcomponent
interface TaskDetailsFragmentSubcomponent : AndroidInjector<TaskDetailsFragment> {
    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<TaskDetailsFragment>()
}

@Subcomponent
interface UserDetailsFragmentSubcomponent : AndroidInjector<UserDetailsFragment> {
    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<UserDetailsFragment>()
}

@Subcomponent
interface UserStatsFragmentSubcomponent : AndroidInjector<UserStatsFragment> {
    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<UserStatsFragment>()
}

@Subcomponent
interface UserOnMapFragmentSubcomponent : AndroidInjector<UserOnMapFragment> {
    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<UserOnMapFragment>()
}
