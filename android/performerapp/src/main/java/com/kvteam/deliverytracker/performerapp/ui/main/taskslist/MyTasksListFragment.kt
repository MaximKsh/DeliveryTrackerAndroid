package com.kvteam.deliverytracker.performerapp.ui.main.taskslist

import android.os.Bundle
import com.kvteam.deliverytracker.core.async.invokeAsync
import com.kvteam.deliverytracker.core.common.IErrorManager
import com.kvteam.deliverytracker.core.ui.ErrorDialog
import com.kvteam.deliverytracker.performerapp.tasks.ITaskRepository
import kotlinx.android.synthetic.main.fragment_tasks_list.*
import javax.inject.Inject

class MyTasksListFragment: TasksListFragment() {
    @Inject
    lateinit var taskRepository: ITaskRepository

    @Inject
    lateinit var errorManager: IErrorManager

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if(savedInstanceState != null
                && !ignoreSavedState) {
            return
        }
        ignoreSavedState = false

        invokeAsync({
            taskRepository.getMyTasks()
        }, {
            if(it.success) {
                adapter.value?.items?.addAll(it.entity!!)
                adapter.value?.notifyDataSetChanged()
            } else {
                val dialog = ErrorDialog(this@MyTasksListFragment.context)
                if(it.errorChainId != null) {
                    dialog.addChain(errorManager.getAndRemove(it.errorChainId!!)!!)
                }
                dialog.show()
            }
        })

        srlSwipeRefreshTasks.setOnRefreshListener { refresh() }
    }

    private fun refresh() {
        invokeAsync({
            taskRepository.getMyTasks(true)
        }, {
            if(it.success) {
                adapter.value?.items?.clear()
                adapter.value?.items?.addAll(it.entity!!)
                adapter.value?.notifyDataSetChanged()
            } else {
                val dialog = ErrorDialog(this@MyTasksListFragment.context)
                if(it.errorChainId != null) {
                    dialog.addChain(errorManager.getAndRemove(it.errorChainId!!)!!)
                }
                dialog.show()
            }
            srlSwipeRefreshTasks.isRefreshing = false
        })
    }
}