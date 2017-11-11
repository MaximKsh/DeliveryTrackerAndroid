package com.kvteam.deliverytracker.performerapp.ui.main.taskslist

import android.os.Bundle
import com.kvteam.deliverytracker.core.async.invokeAsync
import com.kvteam.deliverytracker.performerapp.tasks.ITaskRepository
import javax.inject.Inject

class MyTasksListFragment: TasksListFragment() {
    @Inject
    lateinit var taskRepository: ITaskRepository

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if(savedInstanceState != null) {
            return
        }

        invokeAsync({
            taskRepository.getMyTasks()
        }, {
            if(it != null) {
                adapter.value?.items?.addAll(it)
                adapter.value?.notifyDataSetChanged()
            }
        })
    }
}