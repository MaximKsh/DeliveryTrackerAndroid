package com.kvteam.deliverytracker.performerapp.ui.main.taskslist


import com.kvteam.deliverytracker.core.ui.IBaseListItemActions
import com.kvteam.deliverytracker.core.ui.tasks.BaseTasksListFragment
import com.kvteam.deliverytracker.core.ui.tasks.TaskListItem
import com.kvteam.deliverytracker.performerapp.R
import com.kvteam.deliverytracker.performerapp.ui.main.NavigationController
import eu.davidea.flexibleadapter.FlexibleAdapter
import javax.inject.Inject

open class TasksListFragment : BaseTasksListFragment() {
    @Inject
    lateinit var navigationController: NavigationController

    override val tracer
            get() = navigationController.fragmentTracer


    override val defaultHeader by lazy { lm.getString(R.string.ServerMessage_Views_ActualTasksPerformerView) }

    override val tasksActions = object : IBaseListItemActions<TaskListItem> {
        override suspend fun onDelete(adapter: FlexibleAdapter<*>, itemList: MutableList<TaskListItem>, item: TaskListItem) {}

        override suspend fun onItemClicked(adapter: FlexibleAdapter<*>, itemList: MutableList<TaskListItem>, item: TaskListItem) {
            val id = item.task.id
            if(id != null) {
                 navigationController.navigateToTaskDetails(id)
            }
        }
    }
}
