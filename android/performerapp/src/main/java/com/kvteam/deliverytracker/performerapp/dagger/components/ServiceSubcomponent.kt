package com.kvteam.deliverytracker.performerapp.dagger.components

import com.kvteam.deliverytracker.performerapp.notification.PerformerFirebaseInstanceIdService
import com.kvteam.deliverytracker.performerapp.notification.PerformerFirebaseMessageService
import dagger.Subcomponent
import dagger.android.AndroidInjector

@Subcomponent
interface PerformerFirebaseInstanceIdServiceSubcomponent
    : AndroidInjector<PerformerFirebaseInstanceIdService> {
    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<PerformerFirebaseInstanceIdService>()
}

@Subcomponent
interface PerformerFirebaseMessageServiceSubcomponent
    : AndroidInjector<PerformerFirebaseMessageService> {
    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<PerformerFirebaseMessageService>()
}