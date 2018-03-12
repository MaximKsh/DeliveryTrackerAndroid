package com.kvteam.deliverytracker.managerapp.dagger.components

import com.kvteam.deliverytracker.managerapp.ui.main.userslist.AddUserFragment
import com.kvteam.deliverytracker.managerapp.ui.main.referenceslist.ReferenceListFragment
import com.kvteam.deliverytracker.managerapp.ui.main.referenceslist.clients.AddClientFragment
import com.kvteam.deliverytracker.managerapp.ui.main.referenceslist.clients.EditClientAddressFragment
import com.kvteam.deliverytracker.managerapp.ui.main.referenceslist.paymenttypes.AddPaymentTypeFragment
import com.kvteam.deliverytracker.managerapp.ui.main.referenceslist.products.EditProductFragment
import com.kvteam.deliverytracker.managerapp.ui.main.referenceslist.warehouses.EditWarehouseFragment
import com.kvteam.deliverytracker.managerapp.ui.main.settings.EditSettingsFragment
import com.kvteam.deliverytracker.managerapp.ui.main.settings.SettingsFragment
import com.kvteam.deliverytracker.managerapp.ui.main.taskdetails.SelectPerformerFragment
import com.kvteam.deliverytracker.managerapp.ui.main.taskdetails.TaskDetailsFragment
import com.kvteam.deliverytracker.managerapp.ui.main.taskslist.EditTaskFragment
import com.kvteam.deliverytracker.managerapp.ui.main.taskslist.TasksListFragment
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
interface AddClientSubcomponent : AndroidInjector<AddClientFragment> {
    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<AddClientFragment>()
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
interface AddTaskFragmentSubcomponent : AndroidInjector<TaskDetailsFragment> {
    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<TaskDetailsFragment>()
}

@Subcomponent
interface SelectPerformerFragmentSubcomponent : AndroidInjector<SelectPerformerFragment> {
    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<SelectPerformerFragment>()
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
