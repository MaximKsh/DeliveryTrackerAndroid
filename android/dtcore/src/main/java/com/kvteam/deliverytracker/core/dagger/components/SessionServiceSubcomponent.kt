package com.kvteam.deliverytracker.core.dagger.components

import com.kvteam.deliverytracker.core.session.SessionService
import dagger.Subcomponent
import dagger.android.AndroidInjector

@Subcomponent
interface SessionServiceSubcomponent : AndroidInjector<SessionService> {
    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<SessionService>()
}