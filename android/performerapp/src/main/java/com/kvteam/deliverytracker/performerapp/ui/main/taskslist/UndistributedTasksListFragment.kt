package com.kvteam.deliverytracker.performerapp.ui.main.taskslist

import android.os.Bundle
import kotlinx.android.synthetic.main.fragment_tasks_list.*

class UndistributedTasksListFragment: TasksListFragment() {


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if(savedInstanceState != null
                && !ignoreSavedState) {
            return
        }
        ignoreSavedState = false

        /*invokeAsync({
            taskRepository.getUndistributedTasks()
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
            taskRepository.getUndistributedTasks(true)
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