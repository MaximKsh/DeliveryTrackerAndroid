package com.kvteam.deliverytracker.managerapp.dagger.modules

import com.kvteam.deliverytracker.core.dagger.modules.ServiceModules
import com.kvteam.deliverytracker.core.dagger.scopes.ActivityScope
import com.kvteam.deliverytracker.core.dagger.scopes.ServiceScope
import com.kvteam.deliverytracker.core.session.SessionService
import com.kvteam.deliverytracker.managerapp.ui.approveuserinfo.ApproveUserInfoActivity
import com.kvteam.deliverytracker.managerapp.ui.login.addcompany.AddCompanyActivity
import com.kvteam.deliverytracker.managerapp.ui.login.LoginActivity
import com.kvteam.deliverytracker.managerapp.ui.main.MainActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector


@Module
internal abstract class AndroidBindingModule {
    @ActivityScope
    @ContributesAndroidInjector(modules = arrayOf(LoginActivityModule::class))
    internal abstract fun loginActivity(): LoginActivity

    @ActivityScope
    @ContributesAndroidInjector(modules = arrayOf(AddCompanyActivityModule::class))
    internal abstract fun addCompanyActivity(): AddCompanyActivity

    @ActivityScope
    @ContributesAndroidInjector(modules = arrayOf(MainActivityModule::class))
    internal abstract fun mainActivity(): MainActivity

    @ActivityScope
    @ContributesAndroidInjector(modules = arrayOf(ApproveUserInfoActivityModule::class))
    internal abstract fun approveUserInfoActivity(): ApproveUserInfoActivity

    @ServiceScope
    @ContributesAndroidInjector(modules = arrayOf(ServiceModules::class))
    internal abstract fun sessionService(): SessionService
}