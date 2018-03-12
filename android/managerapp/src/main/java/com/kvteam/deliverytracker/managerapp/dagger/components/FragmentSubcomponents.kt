package com.kvteam.deliverytracker.managerapp.dagger.components

import com.kvteam.deliverytracker.managerapp.ui.main.userslist.AddUserFragment
import com.kvteam.deliverytracker.managerapp.ui.main.referenceslist.ReferenceListFragment
import com.kvteam.deliverytracker.managerapp.ui.main.referenceslist.clients.AddClientFragment
import com.kvteam.deliverytracker.managerapp.ui.main.referenceslist.clients.EditClientAddressFragment
import com.kvteam.deliverytracker.managerapp.ui.main.referenceslist.paymenttypes.AddPaymentTypeFragment
import com.kvteam.deliverytracker.managerapp.ui.main.referenceslist.products.EditProductFragment
import com.kvteam.deliverytracker.managerapp.ui.main.referenceslist.warehouses.EditWarehouseFragment
import com.kvteam.deliverytracker.managerapp.ui.main.taskdetails.SelectPerformerFragment
import com.kvteam.deliverytracker.managerapp.ui.main.taskdetails.TaskDetailsFragment
import com.kvteam.deliverytracker.managerapp.ui.main.taskslist.AllTasksListFragment
import com.kvteam.deliverytracker.managerapp.ui.main.taskslist.MyTasksListFragment
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
interface AllTasksListFragmentSubcomponent : AndroidInjector<AllTasksListFragment> {
    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<AllTasksListFragment>()
}

@Subcomponent
interface MyTasksListFragmentSubcomponent : AndroidInjector<MyTasksListFragment> {
    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<MyTasksListFragment>()
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
