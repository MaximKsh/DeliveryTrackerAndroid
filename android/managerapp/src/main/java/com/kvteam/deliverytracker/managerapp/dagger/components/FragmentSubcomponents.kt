package com.kvteam.deliverytracker.managerapp.dagger.components

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
import com.kvteam.deliverytracker.managerapp.ui.main.userslist.FilterUsersFragment
import com.kvteam.deliverytracker.managerapp.ui.main.userslist.UsersListFragment
import dagger.Subcomponent
import dagger.android.AndroidInjector


@Subcomponent
interface AddUserFragmentSubcomponent : AndroidInjector<AddUserFragment> {
    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<AddUserFragment>()
}

@Subcomponent
interface AddPaymentTypeSubcomponent : AndroidInjector<AddPaymentTypeFragment> {
    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<AddPaymentTypeFragment>()
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
interface EditWarehouseSubcomponent : AndroidInjector<EditWarehouseFragment> {
    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<EditWarehouseFragment>()
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
interface TaskDetailsFragmentSubcomponent : AndroidInjector<TaskDetailsFragment> {
    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<TaskDetailsFragment>()
}
