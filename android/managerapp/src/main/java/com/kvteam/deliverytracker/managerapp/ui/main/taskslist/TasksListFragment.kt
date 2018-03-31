package com.kvteam.deliverytracker.managerapp.ui.main.taskslist


import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import com.kvteam.deliverytracker.core.models.TaskInfo
import com.kvteam.deliverytracker.core.ui.BaseListFragment
import com.kvteam.deliverytracker.core.ui.BaseListHeader
import com.kvteam.deliverytracker.core.ui.IBaseListItemActions
import com.kvteam.deliverytracker.managerapp.R
import com.kvteam.deliverytracker.managerapp.ui.main.NavigationController
import eu.davidea.flexibleadapter.FlexibleAdapter
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import org.joda.time.Duration
import javax.inject.Inject

open class TasksListFragment : BaseListFragment() {
    @Inject
    lateinit var navigationController: NavigationController

    override val tracer
            get() = navigationController.fragmentTracer

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
                    when (duration.standardDays) {
                        in 0..7 -> {
                            header = headerThisWeek
                        }
                        in 8..14 -> {
                            header = headerPreviousWeek
                        }
                        else -> {
                            header = headerLongTimeAgo
                        }
                    }
                    TaskListItem(task, header, lm, dp, context!!)
                }.toMutableList()
        val adapter = mAdapter as? TasksListFlexibleAdapter
        if (adapter != null) {
            adapter.updateDataSet(list, animate)
        } else {
            mAdapter = TasksListFlexibleAdapter(list, tasksActions)
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
        mAdapter = TasksListFlexibleAdapter(mutableListOf(), tasksActions)
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
