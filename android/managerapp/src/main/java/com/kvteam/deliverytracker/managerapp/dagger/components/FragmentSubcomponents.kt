package com.kvteam.deliverytracker.managerapp.dagger.components

import com.kvteam.deliverytracker.managerapp.ui.createinstance.LocationFragment
import com.kvteam.deliverytracker.managerapp.ui.main.adduser.AddUserFragment
import com.kvteam.deliverytracker.managerapp.ui.main.taskdetails.SelectPerformerFragment
import com.kvteam.deliverytracker.managerapp.ui.main.taskdetails.TaskDetailsFragment
import com.kvteam.deliverytracker.managerapp.ui.main.taskslist.AllTasksListFragment
import com.kvteam.deliverytracker.managerapp.ui.main.taskslist.MyTasksListFragment
import com.kvteam.deliverytracker.managerapp.ui.main.userslist.ManagersListFragment
import com.kvteam.deliverytracker.managerapp.ui.main.userslist.PerformersListFragment
import com.kvteam.deliverytracker.managerapp.ui.main.userslist.UsersListFragment
import dagger.Subcomponent
import dagger.android.AndroidInjector

@Subcomponent
interface LocationFragmentSubcomponent : AndroidInjector<LocationFragment> {
    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<LocationFragment>()
}

@Subcomponent
interface AddUserFragmentSubcomponent : AndroidInjector<AddUserFragment> {
    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<AddUserFragment>()
}

@Subcomponent
interface ManagersListFragmentSubcomponent : AndroidInjector<ManagersListFragment> {
    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<ManagersListFragment>()
}

@Subcomponent
interface UsersListFragmentSubcomponent : AndroidInjector<UsersListFragment> {
    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<UsersListFragment>()
}

@Subcomponent
interface PerformersListFragmentSubcomponent : AndroidInjector<PerformersListFragment> {
    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<PerformersListFragment>()
}


@Subcomponent
interface AllTasksListFragmentSubcomponent : AndroidInjector<AllTasksListFragment> {
    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<AllTasksListFragment>()
}

@Subcomponent
interface MyTasksListFragmentSubcomponent : AndroidInjector<MyTasksListFragment> {
    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<MyTasksListFragment>()
}

@Subcomponent
interface AddTaskFragmentSubcomponent : AndroidInjector<TaskDetailsFragment> {
    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<TaskDetailsFragment>()
}

@Subcomponent
interface SelectPerformerFragmentSubcomponent : AndroidInjector<SelectPerformerFragment> {
    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<SelectPerformerFragment>()
}
