package com.kvteam.deliverytracker.managerapp.dagger.modules

import android.support.v4.app.Fragment
import com.kvteam.deliverytracker.managerapp.dagger.components.*
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

@Module(subcomponents = arrayOf(TasksListFragmentSubcomponent::class))
abstract class TasksListFragmentModule {
    @Binds
    @IntoMap
    @FragmentKey(TasksListFragment::class)
    internal abstract fun tasksListFragmentInjector(builder: TasksListFragmentSubcomponent.Builder):
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

@Module(subcomponents = arrayOf(EditTaskSubcomponent::class))
abstract class EditTaskFragmentModule {
    @Binds
    @IntoMap
    @FragmentKey(EditTaskFragment::class)
    internal abstract fun editTaskFragmentInjector(builder: EditTaskSubcomponent.Builder):
            AndroidInjector.Factory<out Fragment>

}

@Module(subcomponents = arrayOf(TaskNumberAndDetailsSubcomponent::class))
abstract class TaskNumberAndDetailsFragmentModule {
    @Binds
    @IntoMap
    @FragmentKey(TaskNumberAndDetailsFragment::class)
    internal abstract fun taskNumberAndDetailsFragmentInjector(builder: TaskNumberAndDetailsSubcomponent.Builder):
            AndroidInjector.Factory<out Fragment>
}

@Module(subcomponents = arrayOf(TaskProductsSubcomponent::class))
abstract class TaskProductsFragmentModule {
    @Binds
    @IntoMap
    @FragmentKey(TaskProductsFragment::class)
    internal abstract fun taskProductsFragmentInjector(builder: TaskProductsSubcomponent.Builder):
            AndroidInjector.Factory<out Fragment>
}

@Module(subcomponents = arrayOf(TaskClientSubcomponent::class))
abstract class TaskClientFragmentModule {
    @Binds
    @IntoMap
    @FragmentKey(TaskClientFragment::class)
    internal abstract fun taskClientFragmentInjector(builder: TaskClientSubcomponent.Builder):
            AndroidInjector.Factory<out Fragment>
}

@Module(subcomponents = arrayOf(TaskDeliveryDateSubcomponent::class))
abstract class TaskDeliveryDateFragmentModule {
    @Binds
    @IntoMap
    @FragmentKey(TaskDeliveryDateFragment::class)
    internal abstract fun taskDeliveryDateFragmentInjector(builder: TaskDeliveryDateSubcomponent.Builder):
            AndroidInjector.Factory<out Fragment>
}

@Module(subcomponents = arrayOf(TaskReceiptAtSubcomponent::class))
abstract class TaskReceiptAtFragmentModule {
    @Binds
    @IntoMap
    @FragmentKey(TaskReceiptAtFragment::class)
    internal abstract fun taskReceiptAtFragmentInjector(builder: TaskReceiptAtSubcomponent.Builder):
            AndroidInjector.Factory<out Fragment>
}

@Module(subcomponents = arrayOf(AddPaymentTypeSubcomponent::class))
abstract class AddPaymentTypeFragmentModule {
    @Binds
    @IntoMap
    @FragmentKey(EditPaymentTypeFragment::class)
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

@Module(subcomponents = arrayOf(FilterProductsSubcomponent::class))
abstract class FilterProductsFragmentModule {
    @Binds
    @IntoMap
    @FragmentKey(FilterProductsFragment::class)
    internal abstract fun filterProductsFragmentInjector(builder: FilterProductsSubcomponent.Builder):
            AndroidInjector.Factory<out Fragment>
}

@Module(subcomponents = arrayOf(FilterUsersSubcomponent::class))
abstract class FilterUsersFragmentModule {
    @Binds
    @IntoMap
    @FragmentKey(FilterUsersFragment::class)
    internal abstract fun filterUsersFragmentInjector(builder: FilterUsersSubcomponent.Builder):
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

@Module(subcomponents = arrayOf(EditClientSubcomponent::class))
abstract class EditClientFragmentModule {
    @Binds
    @IntoMap
    @FragmentKey(EditClientFragment::class)
    internal abstract fun editClientFragmentInjector(builder: EditClientSubcomponent.Builder):
            AndroidInjector.Factory<out Fragment>
}

@Module(subcomponents = arrayOf(ClientDetailsSubcomponent::class))
abstract class ClientDetailsFragmentModule {
    @Binds
    @IntoMap
    @FragmentKey(ClientDetailsFragment::class)
    internal abstract fun clientDetailsFragmentInjector(builder: ClientDetailsSubcomponent.Builder):
            AndroidInjector.Factory<out Fragment>
}

@Module(subcomponents = arrayOf(ProductDetailsSubcomponent::class))
abstract class ProductDetailsFragmentModule {
    @Binds
    @IntoMap
    @FragmentKey(ProductDetailsFragment::class)
    internal abstract fun productDetailsFragmentInjector(builder: ProductDetailsSubcomponent.Builder):
            AndroidInjector.Factory<out Fragment>
}

@Module(subcomponents = arrayOf(PaymentTypeDetailsSubcomponent::class))
abstract class PaymentTypeDetailsFragmentModule {
    @Binds
    @IntoMap
    @FragmentKey(PaymentTypeDetailsFragment::class)
    internal abstract fun paymentTypeDetailsFragmentInjector(builder: PaymentTypeDetailsSubcomponent.Builder):
            AndroidInjector.Factory<out Fragment>
}

@Module(subcomponents = arrayOf(WarehouseDetailsSubcomponent::class))
abstract class WarehouseDetailsFragmentModule {
    @Binds
    @IntoMap
    @FragmentKey(WarehouseDetailsFragment::class)
    internal abstract fun warehouseDetailsFragmentInjector(builder: WarehouseDetailsSubcomponent.Builder):
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

@Module(subcomponents = arrayOf(SettingsFragmentSubcomponent::class))
abstract class SettingsFragmentModule {
    @Binds
    @IntoMap
    @FragmentKey(SettingsFragment::class)
    internal abstract fun settingsFragmentInjector(builder: SettingsFragmentSubcomponent.Builder):
            AndroidInjector.Factory<out Fragment>
}


@Module(subcomponents = arrayOf(EditSettingsFragmentSubcomponent::class))
abstract class EditSettingsFragmentModule {
    @Binds
    @IntoMap
    @FragmentKey(EditSettingsFragment::class)
    internal abstract fun editSettingsFragmentInjector(builder: EditSettingsFragmentSubcomponent.Builder):
            AndroidInjector.Factory<out Fragment>
}

@Module(subcomponents = arrayOf(ChangePasswordFragmentSubcomponent::class))
abstract class ChangePasswordFragmentModule {
    @Binds
    @IntoMap
    @FragmentKey(ChangePasswordFragment::class)
    internal abstract fun injector(builder: ChangePasswordFragmentSubcomponent.Builder):
            AndroidInjector.Factory<out Fragment>
}

@Module(subcomponents = arrayOf(TaskDetailsFragmentSubcomponent::class))
abstract class TaskDetailsFragmentModule {
    @Binds
    @IntoMap
    @FragmentKey(TaskDetailsFragment::class)
    internal abstract fun taskDetailsFragmentInjector(builder: TaskDetailsFragmentSubcomponent.Builder):
            AndroidInjector.Factory<out Fragment>
}

@Module(subcomponents = arrayOf(UserDetailsFragmentSubcomponent::class))
abstract class UserDetailsFragmentModule {
    @Binds
    @IntoMap
    @FragmentKey(UserDetailsFragment::class)
    internal abstract fun userDetailsFragmentInjector(builder: UserDetailsFragmentSubcomponent.Builder):
            AndroidInjector.Factory<out Fragment>
}

@Module(subcomponents = arrayOf(UserTasksListFragmentSubcomponent::class))
abstract class UserTasksListFragmentModule {
    @Binds
    @IntoMap
    @FragmentKey(UserTasksListFragment::class)
    internal abstract fun userTasksListFragmentInjector(builder: UserTasksListFragmentSubcomponent.Builder):
            AndroidInjector.Factory<out Fragment>
}

@Module(subcomponents = arrayOf(UserStatsFragmentSubcomponent::class))
abstract class UserStatsFragmentModule {
    @Binds
    @IntoMap
    @FragmentKey(UserStatsFragment::class)
    internal abstract fun injector(builder: UserStatsFragmentSubcomponent.Builder):
            AndroidInjector.Factory<out Fragment>
}

@Module(subcomponents = arrayOf(UserOnMapFragmentSubcomponent::class))
abstract class UserOnMapFragmentModule {
    @Binds
    @IntoMap
    @FragmentKey(UserOnMapFragment::class)
    internal abstract fun injector(builder: UserOnMapFragmentSubcomponent.Builder):
            AndroidInjector.Factory<out Fragment>
}
