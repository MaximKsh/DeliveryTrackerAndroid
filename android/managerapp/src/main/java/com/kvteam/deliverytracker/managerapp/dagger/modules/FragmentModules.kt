@file:Suppress("unused")

package com.kvteam.deliverytracker.managerapp.dagger.modules

import android.support.v4.app.Fragment
import com.kvteam.deliverytracker.managerapp.dagger.components.*
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
import dagger.Binds
import dagger.Module
import dagger.android.AndroidInjector
import dagger.android.support.FragmentKey
import dagger.multibindings.IntoMap


@Module(subcomponents = arrayOf(UsersListFragmentSubcomponent::class))
abstract class UsersListFragmentModule {
    @Binds
    @IntoMap
    @FragmentKey(UsersListFragment::class)
    internal abstract fun usersListFragmentInjector(builder: UsersListFragmentSubcomponent.Builder):
            AndroidInjector.Factory<out Fragment>

}

@Module(subcomponents = arrayOf(AddUserFragmentSubcomponent::class))
abstract class AddUserFragmentModule {
    @Binds
    @IntoMap
    @FragmentKey(AddUserFragment::class)
    internal abstract fun addUserFragmentInjector(builder: AddUserFragmentSubcomponent.Builder):
            AndroidInjector.Factory<out Fragment>

}

@Module(subcomponents = arrayOf(EditClientAddressSubcomponent::class))
abstract class EditClientAddressFragmentModule {
    @Binds
    @IntoMap
    @FragmentKey(EditClientAddressFragment::class)
    internal abstract fun editClientAddressFragmentInjector(builder: EditClientAddressSubcomponent.Builder):
            AndroidInjector.Factory<out Fragment>

}

@Module(subcomponents = arrayOf(AddPaymentTypeSubcomponent::class))
abstract class AddPaymentTypeFragmentModule {
    @Binds
    @IntoMap
    @FragmentKey(AddPaymentTypeFragment::class)
    internal abstract fun addPaymentTypeFragmentInjector(builder: AddPaymentTypeSubcomponent.Builder):
            AndroidInjector.Factory<out Fragment>
}

@Module(subcomponents = arrayOf(EditProductSubcomponent::class))
abstract class EditProductFragmentModule {
    @Binds
    @IntoMap
    @FragmentKey(EditProductFragment::class)
    internal abstract fun editProductFragmentInjector(builder: EditProductSubcomponent.Builder):
            AndroidInjector.Factory<out Fragment>
}

@Module(subcomponents = arrayOf(EditWarehouseSubcomponent::class))
abstract class EditWarehouseFragmentModule {
    @Binds
    @IntoMap
    @FragmentKey(EditWarehouseFragment::class)
    internal abstract fun editWarehouseFragmentInjector(builder: EditWarehouseSubcomponent.Builder):
            AndroidInjector.Factory<out Fragment>
}

@Module(subcomponents = arrayOf(AddClientSubcomponent::class))
abstract class AddClientFragmentModule {
    @Binds
    @IntoMap
    @FragmentKey(AddClientFragment::class)
    internal abstract fun addClientFragmentInjector(builder: AddClientSubcomponent.Builder):
            AndroidInjector.Factory<out Fragment>
}


@Module(subcomponents = arrayOf(AllTasksListFragmentSubcomponent::class))
abstract class AllTasksListFragmentModule {
    @Binds
    @IntoMap
    @FragmentKey(AllTasksListFragment::class)
    internal abstract fun undistributedTasksListFragmentInjector(builder: AllTasksListFragmentSubcomponent.Builder):
            AndroidInjector.Factory<out Fragment>
}

@Module(subcomponents = arrayOf(MyTasksListFragmentSubcomponent::class))
abstract class MyTasksListFragmentModule {
    @Binds
    @IntoMap
    @FragmentKey(MyTasksListFragment::class)
    internal abstract fun myTasksInjector(builder: MyTasksListFragmentSubcomponent.Builder):
            AndroidInjector.Factory<out Fragment>
}

@Module(subcomponents = arrayOf(AddTaskFragmentSubcomponent::class))
abstract class AddTaskFragmentModule {
    @Binds
    @IntoMap
    @FragmentKey(TaskDetailsFragment::class)
    internal abstract fun addTaskFragmentInjector(builder: AddTaskFragmentSubcomponent.Builder):
            AndroidInjector.Factory<out Fragment>
}

@Module(subcomponents = arrayOf(SelectPerformerFragmentSubcomponent::class))
abstract class SelectPerformerFragmentModule {
    @Binds
    @IntoMap
    @FragmentKey(SelectPerformerFragment::class)
    internal abstract fun selectPerformerFragmentInjector(builder: SelectPerformerFragmentSubcomponent.Builder):
            AndroidInjector.Factory<out Fragment>
}

@Module(subcomponents = arrayOf(ReferenceListFragmentSubcomponent::class))
abstract class ReferenceListFragmentModule {
    @Binds
    @IntoMap
    @FragmentKey(ReferenceListFragment::class)
    internal abstract fun referenceListFragmentInjector(builder: ReferenceListFragmentSubcomponent.Builder):
            AndroidInjector.Factory<out Fragment>
}
