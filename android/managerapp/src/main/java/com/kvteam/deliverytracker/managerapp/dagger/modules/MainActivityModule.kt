package com.kvteam.deliverytracker.managerapp.dagger.modules

import android.app.Activity
import com.kvteam.deliverytracker.core.dagger.scopes.FragmentScope
import com.kvteam.deliverytracker.managerapp.ui.main.MainActivity
import com.kvteam.deliverytracker.managerapp.dagger.components.MainActivitySubcomponent
import com.kvteam.deliverytracker.managerapp.ui.main.adduser.AddUserFragment
import com.kvteam.deliverytracker.managerapp.ui.main.userslist.ManagersListFragment
import dagger.Binds
import dagger.Module
import dagger.android.ActivityKey
import dagger.android.AndroidInjector
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap


@Module(subcomponents = arrayOf(MainActivitySubcomponent::class))
abstract class MainActivityModule {
    @Binds
    @IntoMap
    @ActivityKey(MainActivity::class)
    internal abstract fun mainActivityInjector(builder: MainActivitySubcomponent.Builder):
            AndroidInjector.Factory<out Activity>


    @FragmentScope
    @ContributesAndroidInjector(modules = arrayOf(ManagersListFragmentModule::class))
    internal abstract fun managersListFragment(): ManagersListFragment

    @FragmentScope
    @ContributesAndroidInjector(modules = arrayOf(AddUserFragmentModule::class))
    internal abstract fun addUserFragment(): AddUserFragment
}
