package com.kvteam.deliverytracker.performerapp.ui.main.taskslist


import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kvteam.deliverytracker.core.models.TaskModel
import com.kvteam.deliverytracker.core.ui.AutoClearedValue
import com.kvteam.deliverytracker.core.ui.DeliveryTrackerFragment
import com.kvteam.deliverytracker.performerapp.R
import com.kvteam.deliverytracker.performerapp.ui.main.NavigationController
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_tasks_list.*
import javax.inject.Inject


open class TasksListFragment : DeliveryTrackerFragment() {
    protected val layoutManagerKey = "layoutManager"
    protected val tasksList = "tasksList"

    @Inject
    lateinit var navigationController: NavigationController

    protected lateinit var adapter: AutoClearedValue<TasksListAdapter>

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
        this.rvTasksList.layoutManager = LinearLayoutManager(
                this.activity.applicationContext,
                LinearLayoutManager.VERTICAL,
                false)
        this.rvTasksList.addItemDecoration(
                DividerItemDecoration(this.context, DividerItemDecoration.VERTICAL))

        this.adapter = AutoClearedValue(
                this,
                TasksListAdapter(this::onTaskClicked),
                {
                    it?.onTaskClick = null
                })
        this.rvTasksList.adapter = this.adapter.value
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        if(outState == null) {
            return
        }
        outState.putParcelableArray(
                tasksList,
                this.adapter.value?.items?.toTypedArray())
        outState.putParcelable(
                layoutManagerKey,
                this.rvTasksList.layoutManager.onSaveInstanceState())
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        if(savedInstanceState == null) {
            return
        }

        this.adapter.value?.items?.clear()
        this.adapter.value?.items?.addAll(
                savedInstanceState.getParcelableArray(tasksList).map { it as TaskModel })
        this.rvTasksList.layoutManager.onRestoreInstanceState(
                savedInstanceState.getParcelable(layoutManagerKey))
    }

    private fun onTaskClicked(task: TaskModel) {
        val id = task.id
        if(id != null) {
            navigationController.navigateToTask(id)
        }
    }
}
