package com.kvteam.deliverytracker.performerapp.ui.main.taskslist

import android.os.Bundle
import com.kvteam.deliverytracker.core.async.invokeAsync
import com.kvteam.deliverytracker.performerapp.tasks.ITaskRepository
import javax.inject.Inject

class UndistributedTasksListFragment: TasksListFragment() {
    @Inject
    lateinit var taskRepository: ITaskRepository

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.value?.viewModel?.header?.set("Undistributed Tasks")
        invokeAsync({
            taskRepository.getUndistributedTasks()
        }, {
            adapter.value?.replace(it)
        })
    }
}