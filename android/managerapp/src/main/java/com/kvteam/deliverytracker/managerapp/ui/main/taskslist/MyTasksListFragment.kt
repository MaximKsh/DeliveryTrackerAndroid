package com.kvteam.deliverytracker.managerapp.ui.main.taskslist

import android.os.Bundle
import com.kvteam.deliverytracker.managerapp.R
import kotlinx.android.synthetic.main.fragment_tasks_list.*
import kotlinx.android.synthetic.main.toolbar.*

class MyTasksListFragment: TasksListFragment() {
    //@Inject
    //lateinit var taskRepository: ITaskRepository

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        activity!!.toolbar_title.text = resources.getString(R.string.ManagerApp_MainActivity_MyTasks)
        if(savedInstanceState != null
                && !ignoreSavedState) {
            return
        }
        ignoreSavedState = false

        /*invokeAsync({
            taskRepository.getMyTasks()
        }, {
            if(it.success) {
                adapter.value?.items?.addAll(it.entity!!)
                adapter.value?.notifyDataSetChanged()
            }
        })*/

        srlSwipeRefreshTasks.setOnRefreshListener { refresh() }
    }

    private fun refresh() {
        /*invokeAsync({
            taskRepository.getMyTasks(true)
        }, {
            if(it.success) {
                adapter.value?.items?.clear()
                adapter.value?.items?.addAll(it.entity!!)
                adapter.value?.notifyDataSetChanged()
            }
            srlSwipeRefreshTasks.isRefreshing = false
        })*/
    }
}