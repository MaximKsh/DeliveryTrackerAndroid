package com.kvteam.deliverytracker.managerapp.ui.main.taskslist

import android.os.Bundle
import com.kvteam.deliverytracker.core.async.invokeAsync
import com.kvteam.deliverytracker.managerapp.R
import com.kvteam.deliverytracker.managerapp.tasks.ITaskRepository
import kotlinx.android.synthetic.main.fragment_tasks_list.*
import kotlinx.android.synthetic.main.toolbar.*
import javax.inject.Inject

class AllTasksListFragment : TasksListFragment() {
    @Inject
    lateinit var taskRepository: ITaskRepository

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        activity!!.toolbar_title.text = resources.getString(R.string.ManagerApp_MainActivity_Tasks)
        if(savedInstanceState != null
                && !ignoreSavedState) {
            return
        }
        ignoreSavedState = false

        invokeAsync({
            taskRepository.getAllTasks()
        }, {
            if(it.success) {
                adapter.value?.items?.addAll(it.entity!!)
                adapter.value?.notifyDataSetChanged()
            }
        })

        srlSwipeRefreshTasks.setOnRefreshListener { refresh() }
    }

    private fun refresh() {
        invokeAsync({
            taskRepository.getAllTasks(true)
        }, {
            if(it.success) {
                adapter.value?.items?.clear()
                adapter.value?.items?.addAll(it.entity!!)
                adapter.value?.notifyDataSetChanged()
            }
            srlSwipeRefreshTasks.isRefreshing = false
        })
    }
}