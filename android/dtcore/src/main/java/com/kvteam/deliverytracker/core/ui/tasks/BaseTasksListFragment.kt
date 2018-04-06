package com.kvteam.deliverytracker.core.ui.tasks

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import com.kvteam.deliverytracker.core.R
import com.kvteam.deliverytracker.core.models.TaskInfo
import com.kvteam.deliverytracker.core.ui.BaseListFragment
import com.kvteam.deliverytracker.core.ui.BaseListHeader
import com.kvteam.deliverytracker.core.ui.IBaseListItemActions
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import org.joda.time.Duration

abstract class BaseTasksListFragment : BaseListFragment() {
    override val viewGroup: String = "TaskViewGroup"

    protected abstract val tasksActions : IBaseListItemActions<TaskListItem>

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

    override fun getViewFilterArguments(viewName: String, type: String?, groupIndex: Int, value: String): Map<String, Any>? {
        return mapOf("search" to value)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        mAdapter = TasksListFlexibleAdapter(tasksActions)
        (mAdapter as TasksListFlexibleAdapter).hideDeleteButton = true
        super.onActivityCreated(savedInstanceState)
    }

    override fun refreshMenuItems() {
        if(toolbarController.isSearchEnabled) {
            setMenuMask(0)
        } else {
            setMenuMask(1)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_search -> search()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.search_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }
}
