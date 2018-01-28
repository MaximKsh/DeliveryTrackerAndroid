package com.kvteam.deliverytracker.performerapp.ui.main.task

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kvteam.deliverytracker.core.async.invokeAsync
import com.kvteam.deliverytracker.core.common.EntityResult
import com.kvteam.deliverytracker.core.models.TaskModel
import com.kvteam.deliverytracker.core.tasks.TaskState
import com.kvteam.deliverytracker.core.tasks.toTaskState
import com.kvteam.deliverytracker.core.ui.DeliveryTrackerFragment
import com.kvteam.deliverytracker.performerapp.R
import com.kvteam.deliverytracker.performerapp.tasks.ITaskRepository
import com.kvteam.deliverytracker.performerapp.ui.main.NavigationController
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_task.*
import java.util.*
import javax.inject.Inject

class TaskFragment : DeliveryTrackerFragment() {
    private val taskIdKey = "taskId"
    private val taskKey = "task"

    private lateinit var currentTask: TaskModel

    @Inject
    lateinit var taskRepository: ITaskRepository

    @Inject
    lateinit var navigationController: NavigationController


    lateinit var taskId: UUID
        private set

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidSupportInjection.inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(
                R.layout.fragment_task,
                container,
                false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if(savedInstanceState != null) {
            taskId = savedInstanceState.getSerializable(taskIdKey) as UUID
            val savedTask = savedInstanceState.getParcelable<TaskModel>(taskKey) as TaskModel
            initTask(savedTask)
        } else {
            invokeAsync({
                taskRepository.getTask(taskId)
            }, {
                if(it.success) {
                    initTask(it.entity!!)
                }
            })
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.apply {
            putSerializable(taskIdKey, taskId)
            putParcelable(taskKey, currentTask)
        }
    }


    private fun initTask(task: TaskModel) {
        currentTask = task
        tvTaskNumber.text = task.number
        tvShippingDesc.text = task.shippingDesc
        tvTaskDetails.text = task.details
        tvTaskAddress.text = task.address

        val state = task.state?.toTaskState()
        if(state != null) {
            tvTaskState.text = getString(state.localizationStringId)
            when(state) {
                TaskState.NewUndistributed -> {
                    bttnReserveTask.visibility = View.VISIBLE
                    bttnReserveTask.setOnClickListener {
                        performTaskAction { taskRepository.reserveTask(it) }
                    }
                }
                TaskState.New -> {
                    bttnTakeIntoWorkTask.visibility = View.VISIBLE
                    bttnTakeIntoWorkTask.setOnClickListener {
                        performTaskAction { taskRepository.takeTaskToWork(it) }
                    }
                }
                TaskState.InWork -> {
                    bttnPerformTask.visibility = View.VISIBLE
                    bttnPerformTask.setOnClickListener {
                        performTaskAction { taskRepository.performTask(it) }
                    }
                    bttnCancelTask.visibility = View.VISIBLE
                    bttnCancelTask.setOnClickListener {
                        performTaskAction { taskRepository.cancelTask(it) }
                    }
                }
                else -> {}
            }
        }
    }

    private fun performTaskAction(action: ((taskId: UUID)-> EntityResult<TaskModel?>)) {
        setProcessingState()
        invokeAsync({
            action(taskId)
        }, {
            if(it.success) {
                navigationController.closeCurrentFragment()
            } else {
            }
            setProcessingState(false)
        })
    }

    private fun setProcessingState(processing: Boolean = true){
        bttnReserveTask.isEnabled = !processing
        bttnTakeIntoWorkTask.isEnabled = !processing
        bttnPerformTask.isEnabled = !processing
        bttnCancelTask.isEnabled = !processing
    }

    companion object {
        fun create(taskId: UUID): TaskFragment {
            val fragment = TaskFragment()
            fragment.taskId = taskId
            return fragment
        }
    }
}
