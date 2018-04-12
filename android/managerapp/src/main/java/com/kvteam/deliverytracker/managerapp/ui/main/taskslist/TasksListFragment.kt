package com.kvteam.deliverytracker.managerapp.ui.main.taskslist


import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.view.View
import com.kvteam.deliverytracker.core.ui.IBaseListItemActions
import com.kvteam.deliverytracker.core.ui.tasks.BaseTasksListFragment
import com.kvteam.deliverytracker.core.ui.tasks.TaskListItem
import com.kvteam.deliverytracker.managerapp.R
import com.kvteam.deliverytracker.managerapp.ui.main.NavigationController
import eu.davidea.flexibleadapter.FlexibleAdapter
import javax.inject.Inject

class TasksListFragment : BaseTasksListFragment() {
    @Inject
    lateinit var navigationController: NavigationController

    override val tracer
            get() = navigationController.fragmentTracer

    override val defaultHeader by lazy { lm.getString(R.string.ServerMessage_Views_ActualTasksManagerView) }

    override val tasksActions = object : IBaseListItemActions<TaskListItem> {
        override suspend fun onDelete(adapter: FlexibleAdapter<*>, itemList: MutableList<TaskListItem>, item: TaskListItem) {}

        override suspend fun onItemClicked(adapter: FlexibleAdapter<*>, itemList: MutableList<TaskListItem>, item: TaskListItem) {
            val id = item.task.id
            if(id != null) {
                navigationController.navigateToTaskDetails(id)
            }
        }
    }


    override fun configureFloatingActionButton(button: FloatingActionButton) {
        button.visibility = View.VISIBLE
        button.setImageResource(R.drawable.ic_add_black_24dp)
        button.setOnClickListener {
            navigationController.navigateToEditTask()
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dtActivity.addOnKeyboardHideListener (::showFab)
        dtActivity.addOnKeyboardShowListener (::hideFab)
    }

    override fun onDestroyView() {
        dtActivity.removeOnKeyboardHideListener (::showFab)
        dtActivity.removeOnKeyboardShowListener (::hideFab)
        super.onDestroyView()
    }

}
