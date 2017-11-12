package com.kvteam.deliverytracker.managerapp.dagger.modules

import android.arch.lifecycle.ViewModel
import com.kvteam.deliverytracker.core.dagger.keys.ViewModelKey
import com.kvteam.deliverytracker.managerapp.usersRecycleView.UsersListViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
internal abstract class ViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(UsersListViewModel::class)
    internal abstract fun usersListViewModel(usersListViewModel: UsersListViewModel): ViewModel
}