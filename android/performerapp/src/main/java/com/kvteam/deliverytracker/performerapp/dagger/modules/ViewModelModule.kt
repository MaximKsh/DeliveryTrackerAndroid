package com.kvteam.deliverytracker.performerapp.dagger.modules

import android.arch.lifecycle.ViewModel
import com.kvteam.deliverytracker.core.dagger.keys.ViewModelKey
import com.kvteam.deliverytracker.performerapp.ui.confirm.ConfirmDataViewModel
import com.kvteam.deliverytracker.performerapp.ui.login.LoginViewModel
import com.kvteam.deliverytracker.performerapp.ui.main.MainViewModel
import com.kvteam.deliverytracker.performerapp.ui.main.performerslist.PerformersListViewModel
import com.kvteam.deliverytracker.performerapp.ui.main.task.TaskViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap


@Module
internal abstract class ViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(LoginViewModel::class)
    internal abstract fun loginViewModel(userViewModel: LoginViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ConfirmDataViewModel::class)
    internal abstract fun confirmDataViewModel(confirmDataViewModel: ConfirmDataViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(MainViewModel::class)
    internal abstract fun mainViewModel(mainViewModel: MainViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(TaskViewModel::class)
    internal abstract fun taskViewModel(taskViewModel: TaskViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(PerformersListViewModel::class)
    internal abstract fun performersListViewModel(performersListViewModel: PerformersListViewModel): ViewModel
}
