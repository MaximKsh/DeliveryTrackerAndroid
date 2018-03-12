@file:Suppress("unused")

package com.kvteam.deliverytracker.managerapp.dagger.modules

import android.app.Activity
import com.kvteam.deliverytracker.core.dagger.scopes.ActivityScope
import com.kvteam.deliverytracker.core.dagger.scopes.FragmentScope
import com.kvteam.deliverytracker.core.ui.settings.BaseSettingsFragment
import com.kvteam.deliverytracker.managerapp.dagger.components.ConfirmDataActivitySubcomponent
import com.kvteam.deliverytracker.managerapp.dagger.components.CreateInstanceActivitySubcomponent
import com.kvteam.deliverytracker.managerapp.dagger.components.LoginActivitySubcomponent
import com.kvteam.deliverytracker.managerapp.dagger.components.MainActivitySubcomponent
import com.kvteam.deliverytracker.managerapp.ui.confirm.ConfirmDataActivity
import com.kvteam.deliverytracker.managerapp.ui.createinstance.CreateInstanceActivity
import com.kvteam.deliverytracker.managerapp.ui.login.LoginActivity
import com.kvteam.deliverytracker.managerapp.ui.main.MainActivity
import com.kvteam.deliverytracker.managerapp.ui.main.NavigationController
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
import com.kvteam.deliverytracker.managerapp.ui.main.taskslist.AllTasksListFragment
import com.kvteam.deliverytracker.managerapp.ui.main.taskslist.MyTasksListFragment
import com.kvteam.deliverytracker.managerapp.ui.main.userslist.UsersListFragment
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.android.ActivityKey
import dagger.android.AndroidInjector
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

@Module(subcomponents = arrayOf(CreateInstanceActivitySubcomponent::class))
abstract class CreateInstanceActivityModule {
    @Binds
    @IntoMap
    @ActivityKey(CreateInstanceActivity::class)
    internal abstract fun createInstanceActivityInjector(builder: CreateInstanceActivitySubcomponent.Builder):
            AndroidInjector.Factory<out Activity>


}

@Module(subcomponents = arrayOf(ConfirmDataActivitySubcomponent::class))
abstract class ConfirmDataActivityModule {
    @Binds
    @IntoMap
    @ActivityKey(ConfirmDataActivity::class)
    internal abstract fun approveUserInfoActivityInjector(builder: ConfirmDataActivitySubcomponent.Builder):
            AndroidInjector.Factory<out Activity>

}

@Module(subcomponents = arrayOf(LoginActivitySubcomponent::class))
abstract class LoginActivityModule {
    @Binds
    @IntoMap
    @ActivityKey(LoginActivity::class)
    internal abstract fun loginActivityInjector(builder: LoginActivitySubcomponent.Builder):
            AndroidInjector.Factory<out Activity>

}


@Module(subcomponents = arrayOf(MainActivitySubcomponent::class),
        includes = arrayOf(MainActivityModule.MainActivityNavigationControllerModule::class))
abstract class MainActivityModule {
    @Binds
    @IntoMap
    @ActivityKey(MainActivity::class)
    internal abstract fun mainActivityInjector(builder: MainActivitySubcomponent.Builder):
            AndroidInjector.Factory<out Activity>

    @FragmentScope
    @ContributesAndroidInjector(modules = arrayOf(UsersListFragmentModule::class))
    internal abstract fun usersListFragment(): UsersListFragment

    @FragmentScope
    @ContributesAndroidInjector(modules = arrayOf(AddUserFragmentModule::class))
    internal abstract fun addUserFragment(): AddUserFragment

    @FragmentScope
    @ContributesAndroidInjector(modules = arrayOf(AddPaymentTypeFragmentModule::class))
    internal abstract fun addPaymentTypeFragment(): AddPaymentTypeFragment

    @FragmentScope
    @ContributesAndroidInjector(modules = arrayOf(EditProductFragmentModule::class))
    internal abstract fun editProductFragment(): EditProductFragment

    @FragmentScope
    @ContributesAndroidInjector(modules = arrayOf(EditWarehouseFragmentModule::class))
    internal abstract fun editWarehouseFragment(): EditWarehouseFragment

    @FragmentScope
    @ContributesAndroidInjector(modules = arrayOf(AddClientFragmentModule::class))
    internal abstract fun addClientFragment(): AddClientFragment

    @FragmentScope
    @ContributesAndroidInjector(modules = arrayOf(EditClientAddressFragmentModule::class))
    internal abstract fun editClientAddressFragment(): EditClientAddressFragment

    @FragmentScope
    @ContributesAndroidInjector(modules = arrayOf(AllTasksListFragmentModule::class))
    internal abstract fun allTaskListFragment(): AllTasksListFragment

    @FragmentScope
    @ContributesAndroidInjector(modules = arrayOf(MyTasksListFragmentModule::class))
    internal abstract fun myTasksListFragment(): MyTasksListFragment

    @FragmentScope
    @ContributesAndroidInjector(modules = arrayOf(AddTaskFragmentModule::class))
    internal abstract fun addTaskFragment(): TaskDetailsFragment

    @FragmentScope
    @ContributesAndroidInjector(modules = arrayOf(SelectPerformerFragmentModule::class))
    internal abstract fun selectPerformerFragment(): SelectPerformerFragment

    @FragmentScope
    @ContributesAndroidInjector(modules = arrayOf(ReferenceListFragmentModule::class))
    internal abstract fun referenceListFragment(): ReferenceListFragment

    @FragmentScope
    @ContributesAndroidInjector(modules = arrayOf(SettingsFragmentModule::class))
    internal abstract fun settingsFragment(): SettingsFragment

    @FragmentScope
    @ContributesAndroidInjector(modules = arrayOf(EditSettingsFragmentModule::class))
    internal abstract fun editSettingsFragment(): EditSettingsFragment

    @Module
    class MainActivityNavigationControllerModule {
        @Provides
        @ActivityScope
        fun navigationController(activity: MainActivity): NavigationController {
            return NavigationController(activity)
        }
    }

}
