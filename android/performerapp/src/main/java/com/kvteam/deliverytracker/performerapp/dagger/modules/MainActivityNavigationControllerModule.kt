package com.kvteam.deliverytracker.performerapp.dagger.modules

import com.kvteam.deliverytracker.core.dagger.scopes.ActivityScope
import com.kvteam.deliverytracker.performerapp.ui.main.MainActivity
import com.kvteam.deliverytracker.performerapp.ui.main.NavigationController
import dagger.Module
import dagger.Provides

@Module
class MainActivityNavigationControllerModule {
    @Provides
    @ActivityScope
    fun navigationController(activity: MainActivity): NavigationController {
        return NavigationController(activity)
    }
}