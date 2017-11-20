package com.kvteam.deliverytracker.managerapp.dagger.modules

import android.support.v4.app.Fragment
import com.kvteam.deliverytracker.managerapp.dagger.components.*
import com.kvteam.deliverytracker.managerapp.ui.createinstance.LocationFragment
import com.kvteam.deliverytracker.managerapp.ui.main.addtask.AddTaskFragment
import com.kvteam.deliverytracker.managerapp.ui.main.addtask.SelectPerformerFragment
import com.kvteam.deliverytracker.managerapp.ui.main.adduser.AddUserFragment
import com.kvteam.deliverytracker.managerapp.ui.main.task.TaskFragment
import com.kvteam.deliverytracker.managerapp.ui.main.taskslist.AllTasksListFragment
import com.kvteam.deliverytracker.managerapp.ui.main.taskslist.MyTasksListFragment
import com.kvteam.deliverytracker.managerapp.ui.main.userslist.ManagersListFragment
import com.kvteam.deliverytracker.managerapp.ui.main.userslist.PerformersListFragment
import dagger.Binds
import dagger.Module
import dagger.android.AndroidInjector
import dagger.android.support.FragmentKey
import dagger.multibindings.IntoMap

@Module(subcomponents = arrayOf(LocationFragmentSubcomponent::class))
abstract class LocationFragmentModule {
    @Binds
    @IntoMap
    @FragmentKey(LocationFragment::class)
    internal abstract fun locationFragmentInjector(builder: LocationFragmentSubcomponent.Builder):
            AndroidInjector.Factory<out Fragment>

}

@Module(subcomponents = arrayOf(ManagersListFragmentSubcomponent::class))
abstract class ManagersListFragmentModule {
    @Binds
    @IntoMap
    @FragmentKey(ManagersListFragment::class)
    internal abstract fun managersListFragmentInjector(builder: ManagersListFragmentSubcomponent.Builder):
            AndroidInjector.Factory<out Fragment>

}

@Module(subcomponents = arrayOf(PerformersListFragmentSubcomponent::class))
abstract class PerformersListFragmentModule {
    @Binds
    @IntoMap
    @FragmentKey(PerformersListFragment::class)
    internal abstract fun performersListFragmentInjector(builder: PerformersListFragmentSubcomponent.Builder):
            AndroidInjector.Factory<out Fragment>

}

@Module(subcomponents = arrayOf(AddUserFragmentSubcomponent::class))
abstract class AddUserFragmentModule {
    @Binds
    @IntoMap
    @FragmentKey(AddUserFragment::class)
    internal abstract fun addUserFragmentInjector(builder: AddUserFragmentSubcomponent.Builder):
            AndroidInjector.Factory<out Fragment>

}


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