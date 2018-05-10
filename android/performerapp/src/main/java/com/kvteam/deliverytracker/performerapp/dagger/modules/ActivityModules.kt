package com.kvteam.deliverytracker.performerapp.dagger.modules

import android.app.Activity
import com.kvteam.deliverytracker.core.common.ILocalizationManager
import com.kvteam.deliverytracker.core.common.MapsAdapter
import com.kvteam.deliverytracker.core.dagger.scopes.ActivityScope
import com.kvteam.deliverytracker.core.dagger.scopes.FragmentScope
import com.kvteam.deliverytracker.core.ui.UIState
import com.kvteam.deliverytracker.core.ui.errorhandling.ErrorHandler
import com.kvteam.deliverytracker.core.ui.errorhandling.IErrorHandler
import com.kvteam.deliverytracker.performerapp.R
import com.kvteam.deliverytracker.performerapp.dagger.components.ConfirmDataActivitySubcomponent
import com.kvteam.deliverytracker.performerapp.dagger.components.LoginActivitySubcomponent
import com.kvteam.deliverytracker.performerapp.dagger.components.MainActivitySubcomponent
import com.kvteam.deliverytracker.performerapp.ui.confirm.ConfirmDataActivity
import com.kvteam.deliverytracker.performerapp.ui.login.LoginActivity
import com.kvteam.deliverytracker.performerapp.ui.main.MainActivity
import com.kvteam.deliverytracker.performerapp.ui.main.NavigationController
import com.kvteam.deliverytracker.performerapp.ui.main.settings.ChangePasswordFragment
import com.kvteam.deliverytracker.performerapp.ui.main.settings.EditSettingsFragment
import com.kvteam.deliverytracker.performerapp.ui.main.settings.SettingsFragment
import com.kvteam.deliverytracker.performerapp.ui.main.taskslist.TaskDetailsFragment
import com.kvteam.deliverytracker.performerapp.ui.main.taskslist.TasksListFragment
import com.kvteam.deliverytracker.performerapp.ui.main.userslist.UsersListFragment
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.android.ActivityKey
import dagger.android.AndroidInjector
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

@Module(subcomponents = arrayOf(LoginActivitySubcomponent::class),
        includes = [(LoginActivityModule.ScopeModule::class)])
abstract class LoginActivityModule {
    @Binds
    @IntoMap
    @ActivityKey(LoginActivity::class)
    internal abstract fun loginActivityInjector(builder: LoginActivitySubcomponent.Builder):
            AndroidInjector.Factory<out Activity>

    @Module
    class ScopeModule {
        @Provides
        @ActivityScope
        fun errorHandler(activity: LoginActivity, lm: ILocalizationManager): IErrorHandler {
            return ErrorHandler(
                    activity,
                    0,
                    lm)
        }
    }
}


@Module(subcomponents = arrayOf(ConfirmDataActivitySubcomponent::class),
        includes = [(ConfirmDataActivityModule.ScopeModule::class)])
abstract class ConfirmDataActivityModule {
    @Binds
    @IntoMap
    @ActivityKey(ConfirmDataActivity::class)
    internal abstract fun confirmDataActivity(builder: ConfirmDataActivitySubcomponent.Builder):
            AndroidInjector.Factory<out Activity>

    @Module
    class ScopeModule {
        @Provides
        @ActivityScope
        fun errorHandler(activity: ConfirmDataActivity, lm: ILocalizationManager): IErrorHandler {
            return ErrorHandler(
                    activity,
                    0,
                    lm)
        }
    }
}

@Module(subcomponents = arrayOf(MainActivitySubcomponent::class),
        includes = arrayOf(MainActivityModule.ScopeModule::class))
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
    @ContributesAndroidInjector(modules = arrayOf(SettingsFragmentModule::class))
    internal abstract fun settingsFragment(): SettingsFragment

    @FragmentScope
    @ContributesAndroidInjector(modules = arrayOf(EditSettingsFragmentModule::class))
    internal abstract fun editSettingsFragment(): EditSettingsFragment

    @FragmentScope
    @ContributesAndroidInjector(modules = arrayOf(ChangePasswordSettingsFragmentModule::class))
    internal abstract fun changePasswordFragment(): ChangePasswordFragment

    @FragmentScope
    @ContributesAndroidInjector(modules = arrayOf(TaskDetailsFragmentModule::class))
    internal abstract fun taskDetailsFragment(): TaskDetailsFragment

    @Module
    class ScopeModule {
        @Provides
        @ActivityScope
        fun navigationController(activity: MainActivity): NavigationController {
            return NavigationController(activity)
        }

        @Provides
        @ActivityScope
        fun mapsAdapter(activity: MainActivity): MapsAdapter {
            return MapsAdapter(activity.googleApiClient)
        }

        @Provides
        @ActivityScope
        fun errorHandler(activity: MainActivity, lm: ILocalizationManager): IErrorHandler {
            return ErrorHandler(
                    activity,
                    R.id.placeSnackBar,
                    lm)
        }

        @Provides
        @ActivityScope
        fun uiState() : UIState {
            return UIState()
        }
    }
}


