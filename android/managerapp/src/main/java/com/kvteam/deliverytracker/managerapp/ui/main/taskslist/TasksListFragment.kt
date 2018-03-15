package com.kvteam.deliverytracker.managerapp.ui.main.taskslist


import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.*
import com.kvteam.deliverytracker.core.async.invokeAsync
import com.kvteam.deliverytracker.core.common.EMPTY_STRING
import com.kvteam.deliverytracker.core.common.ILocalizationManager
import com.kvteam.deliverytracker.core.models.TaskInfo
import com.kvteam.deliverytracker.core.ui.*
import com.kvteam.deliverytracker.managerapp.R
import com.kvteam.deliverytracker.core.ui.dropdowntop.DropdownTop
import com.kvteam.deliverytracker.core.ui.dropdowntop.DropdownTopItemInfo
import com.kvteam.deliverytracker.core.ui.dropdowntop.ToolbarController
import com.kvteam.deliverytracker.core.webservice.IReferenceWebservice
import com.kvteam.deliverytracker.core.webservice.ITaskWebservice
import com.kvteam.deliverytracker.managerapp.ui.main.NavigationController
import dagger.android.support.AndroidSupportInjection
import eu.davidea.flexibleadapter.FlexibleAdapter
import javax.inject.Inject

open class TasksListFragment : BaseListFragment() {
    @Inject
    lateinit var navigationController: NavigationController

    @Inject
    lateinit var tasksWebservice: ITaskWebservice

    override val viewGroup: String = "TaskViewGroup"

    private val TASKS_MENU_MASK = 1

    private val tasksActions = object : IBaseListItemActions<TaskListItem> {
        override fun onDelete(adapter: FlexibleAdapter<*>, itemList: MutableList<TaskListItem>, item: TaskListItem) {
            if (adapter !is TasksListFlexibleAdapter) {
                return
            }
//            invokeAsync({
//                tasksWebservice.get("", item.paymentType.id!!)
//            }, {
//                if (it.success) {
                    itemList.remove(item)
                    adapter.updateDataSet(itemList, true)
//                }
            }

        override fun onItemClicked(adapter: FlexibleAdapter<*>, itemList: MutableList<TaskListItem>, item: TaskListItem) {}
    }


    private fun formatTasks(viewResult: List<Map<String, Any?>>): MutableList<TaskListItem> {
        var date: String? = null
        var header = BaseListHeader("A")

        return viewResult
                .map { taskMap ->
                    val task = TaskInfo()
                    task.fromMap(taskMap)
                    task
                }
                .sortedByDescending { a -> a.created }
                .map { task ->
                    val dateCaption = task.created?.toString("dd.MM.yyyy") ?: EMPTY_STRING
                    if (date == null || date != dateCaption) {
                        date = dateCaption
                        header = BaseListHeader(dateCaption)
                    }
                    TaskListItem(task, header, lm)
                }.toMutableList()
    }

    override fun handleUpdateList(type: String, viewResult: List<Map<String, Any?>>) {
        when (type) {
            "TaskInfo" -> {
                val referencesList = formatTasks(viewResult)
                val adapter = mAdapter as? TasksListFlexibleAdapter
                setMenuMask(TASKS_MENU_MASK)
                if (adapter != null) {
                    adapter.updateDataSet(referencesList, true)
                } else {
                    mAdapter = TasksListFlexibleAdapter(referencesList, tasksActions)
                    initAdapter()
                }
            }
        }
    }

    override fun configureToolbar(toolbar: ToolbarController) {
        toolbar.enableDropdown()
        toolbar.dropDownTop.hideSearch()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        mAdapter = TasksListFlexibleAdapter(mutableListOf(), tasksActions)
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
