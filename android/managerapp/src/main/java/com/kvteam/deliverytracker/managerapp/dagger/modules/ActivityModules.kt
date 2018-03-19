@file:Suppress("unused")

package com.kvteam.deliverytracker.managerapp.dagger.modules

import android.app.Activity
import com.kvteam.deliverytracker.core.common.ILocalizationManager
import com.kvteam.deliverytracker.core.dagger.scopes.ActivityScope
import com.kvteam.deliverytracker.core.dagger.scopes.FragmentScope
import com.kvteam.deliverytracker.core.ui.errorhandling.ErrorHandler
import com.kvteam.deliverytracker.core.ui.errorhandling.IErrorHandler
import com.kvteam.deliverytracker.managerapp.R
import com.kvteam.deliverytracker.managerapp.dagger.components.ConfirmDataActivitySubcomponent
import com.kvteam.deliverytracker.managerapp.dagger.components.CreateInstanceActivitySubcomponent
import com.kvteam.deliverytracker.managerapp.dagger.components.LoginActivitySubcomponent
import com.kvteam.deliverytracker.managerapp.dagger.components.MainActivitySubcomponent
import com.kvteam.deliverytracker.managerapp.ui.confirm.ConfirmDataActivity
import com.kvteam.deliverytracker.managerapp.ui.createinstance.CreateInstanceActivity
import com.kvteam.deliverytracker.managerapp.ui.login.LoginActivity
import com.kvteam.deliverytracker.managerapp.ui.main.MainActivity
import com.kvteam.deliverytracker.managerapp.ui.main.NavigationController
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
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.android.ActivityKey
import dagger.android.AndroidInjector
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

@Module(subcomponents = [(CreateInstanceActivitySubcomponent::class)],
        includes = [(CreateInstanceActivityModule.CreateInstanceScopeModule::class)])
abstract class CreateInstanceActivityModule {
    @Binds
    @IntoMap
    @ActivityKey(CreateInstanceActivity::class)
    internal abstract fun createInstanceActivityInjector(builder: CreateInstanceActivitySubcomponent.Builder):
            AndroidInjector.Factory<out Activity>
    @Module
    class CreateInstanceScopeModule {
        @Provides
        @ActivityScope
        fun errorHandler(activity: CreateInstanceActivity, lm: ILocalizationManager): IErrorHandler {
            return ErrorHandler(
                    activity,
                    R.id.llCreateInstanceMainView,
                    lm)
        }
    }

}

@Module(subcomponents = [(ConfirmDataActivitySubcomponent::class)],
        includes = [(ConfirmDataActivityModule.ConfirmDataScopeModule::class)])
abstract class ConfirmDataActivityModule {
    @Binds
    @IntoMap
    @ActivityKey(ConfirmDataActivity::class)
    internal abstract fun approveUserInfoActivityInjector(builder: ConfirmDataActivitySubcomponent.Builder):
            AndroidInjector.Factory<out Activity>

    @Module
    class ConfirmDataScopeModule {
        @Provides
        @ActivityScope
        fun errorHandler(activity: ConfirmDataActivity, lm: ILocalizationManager): IErrorHandler {
            return ErrorHandler(
                    activity,
                    R.id.llConfirmDataMainView,
                    lm)
        }
    }

}

@Module(subcomponents = [(LoginActivitySubcomponent::class)],
        includes = [(LoginActivityModule.LoginActivityScopeModule::class)])
abstract class LoginActivityModule {
    @Binds
    @IntoMap
    @ActivityKey(LoginActivity::class)
    internal abstract fun loginActivityInjector(builder: LoginActivitySubcomponent.Builder):
            AndroidInjector.Factory<out Activity>

    @Module
    class LoginActivityScopeModule {
        @Provides
        @ActivityScope
        fun errorHandler(activity: LoginActivity, lm: ILocalizationManager): IErrorHandler {
            return ErrorHandler(
                    activity,
                    R.id.llLoginActivityRootView,
                    lm)
        }
    }
}


@Module(subcomponents = [(MainActivitySubcomponent::class)],
        includes = [(MainActivityModule.MainActivityScopeModule::class)])
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
    @ContributesAndroidInjector(modules = arrayOf(TasksListFragmentModule::class))
    internal abstract fun tasksListFragment(): TasksListFragment

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
    @ContributesAndroidInjector(modules = arrayOf(EditTaskFragmentModule::class))
    internal abstract fun editTaskFragment(): EditTaskFragment

    @FragmentScope
    @ContributesAndroidInjector(modules = arrayOf(FilterProductsFragmentModule::class))
    internal abstract fun filterProductsFragment(): FilterProductsFragment

    @FragmentScope
    @ContributesAndroidInjector(modules = arrayOf(EditWarehouseFragmentModule::class))
    internal abstract fun editWarehouseFragment(): EditWarehouseFragment

    @FragmentScope
    @ContributesAndroidInjector(modules = arrayOf(EditClientFragmentModule::class))
    internal abstract fun editClientFragment(): EditClientFragment

    @FragmentScope
    @ContributesAndroidInjector(modules = arrayOf(ClientDetailsFragmentModule::class))
    internal abstract fun clientDetailsFragment(): ClientDetailsFragment

    @FragmentScope
    @ContributesAndroidInjector(modules = arrayOf(PaymentTypeDetailsFragmentModule::class))
    internal abstract fun paymentTypeDetailsFragment(): PaymentTypeDetailsFragment

    @FragmentScope
    @ContributesAndroidInjector(modules = arrayOf(EditClientAddressFragmentModule::class))
    internal abstract fun editClientAddressFragment(): EditClientAddressFragment

    @FragmentScope
    @ContributesAndroidInjector(modules = arrayOf(ReferenceListFragmentModule::class))
    internal abstract fun referenceListFragment(): ReferenceListFragment

    @FragmentScope
    @ContributesAndroidInjector(modules = arrayOf(SettingsFragmentModule::class))
    internal abstract fun settingsFragment(): SettingsFragment

    @FragmentScope
    @ContributesAndroidInjector(modules = arrayOf(EditSettingsFragmentModule::class))
    internal abstract fun editSettingsFragment(): EditSettingsFragment

    @FragmentScope
    @ContributesAndroidInjector(modules = arrayOf(TaskDetailsFragmentModule::class))
    internal abstract fun taskDetailsFragment(): TaskDetailsFragment

    @Module
    class MainActivityScopeModule {
        @Provides
        @ActivityScope
        fun navigationController(activity: MainActivity): NavigationController {
            return NavigationController(activity)
        }

        @Provides
        @ActivityScope
        fun errorHandler(activity: MainActivity, lm: ILocalizationManager): IErrorHandler {
            return ErrorHandler(
                    activity,
                    R.id.mainContainer,
                    lm)
        }
    }
}
