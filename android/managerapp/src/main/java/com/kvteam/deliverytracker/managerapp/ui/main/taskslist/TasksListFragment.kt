package com.kvteam.deliverytracker.managerapp.ui.main.taskslist


import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kvteam.deliverytracker.core.models.TaskModel
import com.kvteam.deliverytracker.core.ui.AutoClearedValue
import com.kvteam.deliverytracker.core.ui.DeliveryTrackerFragment
import com.kvteam.deliverytracker.managerapp.R
import com.kvteam.deliverytracker.managerapp.ui.main.NavigationController
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_tasks_list.*
import javax.inject.Inject


open class TasksListFragment : DeliveryTrackerFragment() {
    protected val layoutManagerKey = "layoutManager"
    protected val tasksListKey = "tasksListKey"

    @Inject
    lateinit var navigationController: NavigationController

    protected lateinit var adapter: AutoClearedValue<TasksListAdapter>

    protected var ignoreSavedState = false

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidSupportInjection.inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
            inflater: LayoutInflater?,
            container: ViewGroup?,
            savedInstanceState: Bundle?): View? {
        return inflater?.inflate(
                R.layout.fragment_tasks_list,
                container,
                false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        rvTasksList.layoutManager = LinearLayoutManager(
                activity.applicationContext,
                LinearLayoutManager.VERTICAL,
                false)
        rvTasksList.addItemDecoration(
                DividerItemDecoration(context, DividerItemDecoration.VERTICAL))

        adapter = AutoClearedValue(
                this,
                TasksListAdapter(this::onTaskClicked, context::getString),
                {
                    it?.onTaskClick = null
                    it?.getLocalizedString = null
                })
        rvTasksList.adapter = adapter.value

        bttnAddNewTask.setOnClickListener { navigationController.navigateToAddTask() }

        savedInstanceState?.apply {
            val adapter = adapter.value
            val layoutManager = rvTasksList?.layoutManager
            if(adapter != null
                    && layoutManager != null) {
                if(containsKey(tasksListKey)
                        && containsKey(layoutManagerKey)) {
                    val savedTasks = getParcelableArray(tasksListKey).map { it as TaskModel }
                    adapter.items.clear()
                    adapter.items.addAll(savedTasks)
                    layoutManager.onRestoreInstanceState(getParcelable(layoutManagerKey))
                } else {
                    ignoreSavedState = true
                }
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.apply {
            val adapter = adapter.value
            val layoutManager = rvTasksList?.layoutManager
            if(adapter != null
                    && layoutManager != null) {
                putParcelableArray(
                        tasksListKey,
                        adapter.items.toTypedArray())
                putParcelable(
                        layoutManagerKey,
                        layoutManager.onSaveInstanceState())
            }
        }
    }

    private fun onTaskClicked(task: TaskModel) {
        val id = task.id
        if(id != null) {
            navigationController.navigateToTask(id)
        }
    }

}
