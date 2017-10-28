package com.kvteam.deliverytracker.performerapp.dagger.components

import com.kvteam.deliverytracker.performerapp.ui.main.task.TaskFragment
import dagger.Subcomponent
import dagger.android.AndroidInjector

@Subcomponent
interface TaskFragmentSubcomponent : AndroidInjector<TaskFragment> {
    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<TaskFragment>()
}