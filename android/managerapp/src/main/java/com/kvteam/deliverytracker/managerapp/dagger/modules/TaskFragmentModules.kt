package com.kvteam.deliverytracker.managerapp.dagger.modules

import android.support.v4.app.Fragment
import com.kvteam.deliverytracker.managerapp.dagger.components.*
import com.kvteam.deliverytracker.managerapp.ui.main.addtask.AddTaskFragment
import com.kvteam.deliverytracker.managerapp.ui.main.addtask.SelectPerformerFragment
import com.kvteam.deliverytracker.managerapp.ui.main.task.TaskFragment
import com.kvteam.deliverytracker.managerapp.ui.main.taskslist.AllTasksListFragment
import com.kvteam.deliverytracker.managerapp.ui.main.taskslist.MyTasksListFragment
import dagger.Binds
import dagger.Module
import dagger.android.AndroidInjector
import dagger.android.support.FragmentKey
import dagger.multibindings.IntoMap


@Module(subcomponents = arrayOf(AllTasksListFragmentSubcomponent::class))
abstract class AllTasksListFragmentModule {
    @Binds
    @IntoMap
    @FragmentKey(AllTasksListFragment::class)
    internal abstract fun undistributedTasksListFragmentInjector(builder: AllTasksListFragmentSubcomponent.Builder):
            AndroidInjector.Factory<out Fragment>
}

@Module(subcomponents = arrayOf(MyTasksListFragmentSubcomponent::class))
abstract class MyTasksListFragmentModule {
    @Binds
    @IntoMap
    @FragmentKey(MyTasksListFragment::class)
    internal abstract fun myTasksInjector(builder: MyTasksListFragmentSubcomponent.Builder):
            AndroidInjector.Factory<out Fragment>
}

@Module(subcomponents = arrayOf(AddTaskFragmentSubcomponent::class))
abstract class AddTaskFragmentModule {
    @Binds
    @IntoMap
    @FragmentKey(AddTaskFragment::class)
    internal abstract fun addTaskFragmentInjector(builder: AddTaskFragmentSubcomponent.Builder):
            AndroidInjector.Factory<out Fragment>
}

@Module(subcomponents = arrayOf(SelectPerformerFragmentSubcomponent::class))
abstract class SelectPerformerFragmentModule {
    @Binds
    @IntoMap
    @FragmentKey(SelectPerformerFragment::class)
    internal abstract fun selectPerformerFragmentInjector(builder: SelectPerformerFragmentSubcomponent.Builder):
            AndroidInjector.Factory<out Fragment>
}

@Module(subcomponents = arrayOf(TaskFragmentSubcomponent::class))
abstract class TaskFragmentModule {
    @Binds
    @IntoMap
    @FragmentKey(TaskFragment::class)
    internal abstract fun taskFragmentInjector(builder: TaskFragmentSubcomponent.Builder):
            AndroidInjector.Factory<out Fragment>
}