package com.kvteam.deliverytracker.managerapp.ui.main.taskslist

import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.view.View
import com.kvteam.deliverytracker.core.models.TaskInfo
import com.kvteam.deliverytracker.core.ui.BaseListFragment
import com.kvteam.deliverytracker.core.ui.BaseListHeader
import com.kvteam.deliverytracker.core.ui.IBaseListItemActions
import com.kvteam.deliverytracker.core.ui.tasks.BaseTasksListFragment
import com.kvteam.deliverytracker.core.ui.tasks.TaskListItem
import com.kvteam.deliverytracker.core.ui.tasks.TasksListFlexibleAdapter
import com.kvteam.deliverytracker.core.ui.toolbar.ToolbarController
import com.kvteam.deliverytracker.managerapp.R
import com.kvteam.deliverytracker.managerapp.ui.main.NavigationController
import eu.davidea.flexibleadapter.FlexibleAdapter
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import org.joda.time.Duration
import javax.inject.Inject

class UserTasksListFragment : BaseListFragment() {
    @Inject
    lateinit var navigationController: NavigationController

    override val viewGroup: String = "TaskViewGroup"

    private val tasksActions = object : IBaseListItemActions<TaskListItem> {
        override suspend fun onDelete(adapter: FlexibleAdapter<*>, itemList: MutableList<TaskListItem>, item: TaskListItem) {}

        override suspend fun onItemClicked(adapter: FlexibleAdapter<*>, itemList: MutableList<TaskListItem>, item: TaskListItem) {
            val id = item.task.id
            if(id != null) {
                navigationController.navigateToTaskDetails(id)
            }
        }
    }

    override val tracer
        get() = navigationController.fragmentTracer

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        mAdapter = TasksListFlexibleAdapter(tasksActions)
        (mAdapter as TasksListFlexibleAdapter).hideDeleteButton = true
        super.onActivityCreated(savedInstanceState)
    }

    override fun handleTasks(tasks: List<TaskInfo>, animate: Boolean) {
        val headerThisWeek = BaseListHeader("This week")
        val headerPreviousWeek = BaseListHeader("Previous week")
        // TODO: fix this fate range
        val headerLongTimeAgo = BaseListHeader("Long time ago")

        val list = tasks
                .sortedByDescending { a -> a.created }
                .map { task ->
                    val header: BaseListHeader
                    val dateValue = task.created!!
                    val duration = Duration(dateValue, DateTime.now(DateTimeZone.UTC))
                    header = when (duration.standardDays) {
                        in 0..7 -> {
                            headerThisWeek
                        }
                        in 8..14 -> {
                            headerPreviousWeek
                        }
                        else -> {
                            headerLongTimeAgo
                        }
                    }
                    TaskListItem(task, header, lm, dp, context!!)
                }.toMutableList()
        updateDataSet(list, { TasksListFlexibleAdapter(tasksActions) }, animate)
    }

    override fun configureToolbar(toolbar: ToolbarController) {}
}
