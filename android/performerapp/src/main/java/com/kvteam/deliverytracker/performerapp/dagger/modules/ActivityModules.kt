package com.kvteam.deliverytracker.performerapp.dagger.modules

import android.app.Activity
import com.kvteam.deliverytracker.core.dagger.scopes.ActivityScope
import com.kvteam.deliverytracker.core.dagger.scopes.FragmentScope
import com.kvteam.deliverytracker.performerapp.dagger.components.ConfirmDataActivitySubcomponent
import com.kvteam.deliverytracker.performerapp.dagger.components.LoginActivitySubcomponent
import com.kvteam.deliverytracker.performerapp.dagger.components.MainActivitySubcomponent
import com.kvteam.deliverytracker.performerapp.ui.confirm.ConfirmDataActivity
import com.kvteam.deliverytracker.performerapp.ui.login.LoginActivity
import com.kvteam.deliverytracker.performerapp.ui.main.MainActivity
import com.kvteam.deliverytracker.performerapp.ui.main.NavigationController
import com.kvteam.deliverytracker.performerapp.ui.main.task.TaskFragment
import com.kvteam.deliverytracker.performerapp.ui.main.taskslist.MyTasksListFragment
import com.kvteam.deliverytracker.performerapp.ui.main.taskslist.UndistributedTasksListFragment
import com.kvteam.deliverytracker.performerapp.ui.main.userslist.ManagersListFragment
import com.kvteam.deliverytracker.performerapp.ui.main.userslist.PerformersListFragment
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.android.ActivityKey
import dagger.android.AndroidInjector
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

@Module(subcomponents = arrayOf(ConfirmDataActivitySubcomponent::class))
abstract class ConfirmDataActivityModule {
    @Binds
    @IntoMap
    @ActivityKey(ConfirmDataActivity::class)
    internal abstract fun confirmDataActivity(builder: ConfirmDataActivitySubcomponent.Builder):
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
    @ContributesAndroidInjector(modules = arrayOf(TaskFragmentModule::class))
    internal abstract fun taskFragment(): TaskFragment

    @FragmentScope
    @ContributesAndroidInjector(modules = arrayOf(MyTasksListFragmentModule::class))
    internal abstract fun myTasksListFragment(): MyTasksListFragment

    @FragmentScope
    @ContributesAndroidInjector(modules = arrayOf(UndistributedTasksListFragmentModule::class))
    internal abstract fun undistributedTasksListFragment(): UndistributedTasksListFragment

    @FragmentScope
    @ContributesAndroidInjector(modules = arrayOf(PerformersListFragmentModule::class))
    internal abstract fun performersListFragment(): PerformersListFragment

    @FragmentScope
    @ContributesAndroidInjector(modules = arrayOf(ManagersListFragmentModule::class))
    internal abstract fun managersListFragment(): ManagersListFragment

    @Module
    class MainActivityNavigationControllerModule {
        @Provides
        @ActivityScope
        fun navigationController(activity: MainActivity): NavigationController {
            return NavigationController(activity)
        }
    }
}

@Module(subcomponents = arrayOf(LoginActivitySubcomponent::class))
abstract class LoginActivityModule {
    @Binds
    @IntoMap
    @ActivityKey(LoginActivity::class)
    internal abstract fun loginActivityInjector(builder: LoginActivitySubcomponent.Builder):
            AndroidInjector.Factory<out Activity>
}

