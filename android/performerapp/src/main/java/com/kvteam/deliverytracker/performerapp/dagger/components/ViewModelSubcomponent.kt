package com.kvteam.deliverytracker.performerapp.dagger.components

import dagger.Subcomponent

@Subcomponent
interface ViewModelSubcomponent {
    @Subcomponent.Builder
    interface Builder {
        fun build(): ViewModelSubcomponent
    }
}