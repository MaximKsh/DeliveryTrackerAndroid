package com.kvteam.deliverytracker.managerapp.ui.main.taskslist


import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import com.kvteam.deliverytracker.core.common.EMPTY_STRING
import com.kvteam.deliverytracker.core.models.TaskInfo
import com.kvteam.deliverytracker.core.ui.BaseListFragment
import com.kvteam.deliverytracker.core.ui.BaseListHeader
import com.kvteam.deliverytracker.core.ui.IBaseListItemActions
import com.kvteam.deliverytracker.core.ui.toolbar.ToolbarController
import com.kvteam.deliverytracker.managerapp.R
import com.kvteam.deliverytracker.managerapp.ui.main.NavigationController
import eu.davidea.flexibleadapter.FlexibleAdapter
import javax.inject.Inject

open class TasksListFragment : BaseListFragment() {
    @Inject
    lateinit var navigationController: NavigationController

    override val tracer = navigationController.fragmentTracer

    override val viewGroup: String = "TaskViewGroup"

    private val TASKS_MENU_MASK = 1

    private val tasksActions = object : IBaseListItemActions<TaskListItem> {
        override suspend fun onDelete(adapter: FlexibleAdapter<*>, itemList: MutableList<TaskListItem>, item: TaskListItem) {}

        override suspend fun onItemClicked(adapter: FlexibleAdapter<*>, itemList: MutableList<TaskListItem>, item: TaskListItem) {
            val id = item.task.id
            if(id != null) {
                navigationController.navigateToTaskDetails(id)
            }
        }
    }

    override fun handleTasks(tasks: List<TaskInfo>) {
        var date: String? = null
        var header = BaseListHeader("A")

        val list = tasks
                .sortedByDescending { a -> a.created }
                .map { task ->
                    val dateCaption = task.created?.toString("dd.MM.yyyy") ?: EMPTY_STRING
                    if (date == null || date != dateCaption) {
                        date = dateCaption
                        header = BaseListHeader(dateCaption)
                    }
                    TaskListItem(task, header, lm)
                }.toMutableList()
        val adapter = mAdapter as? TasksListFlexibleAdapter
        setMenuMask(TASKS_MENU_MASK)
        if (adapter != null) {
            adapter.updateDataSet(list, true)
        } else {
            mAdapter = TasksListFlexibleAdapter(list, tasksActions)
            initAdapter()
        }
    }

    override fun configureToolbar(toolbar: ToolbarController) {
        toolbar.enableDropdown()
        toolbar.dropDownTop.hideSearch()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        mAdapter = TasksListFlexibleAdapter(mutableListOf(), tasksActions)
        (mAdapter as TasksListFlexibleAdapter).hideDeleteButton = true
        super.onActivityCreated(savedInstanceState)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_add -> {
                navigationController.navigateToEditTask()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.toolbar_tasks_list_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }
}
