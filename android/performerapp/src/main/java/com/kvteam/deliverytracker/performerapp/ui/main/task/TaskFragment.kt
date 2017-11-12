package com.kvteam.deliverytracker.performerapp.ui.main.task

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.kvteam.deliverytracker.core.async.invokeAsync
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

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater?.inflate(
                R.layout.fragment_task,
                container,
                false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if(savedInstanceState != null) {
            taskId = savedInstanceState.getSerializable(taskIdKey) as UUID
            val savedTask = savedInstanceState.getParcelable<TaskModel>(taskKey) as TaskModel
            this.initTask(savedTask)
        } else {
            invokeAsync({
                this@TaskFragment.taskRepository.getTask(taskId)
            }, {
                if(it != null) {
                    this@TaskFragment.initTask(it)
                }
            })
        }
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.apply {
            putSerializable(taskIdKey, taskId)
            putParcelable(taskKey, currentTask)
        }
    }


    private fun initTask(task: TaskModel) {
        this.currentTask = task
        this.tvTaskNumber.text = task.number
        this.tvShippingDesc.text = task.shippingDesc
        this.tvTaskDetails.text = task.details
        this.tvTaskAddress.text = task.address

        val state = task.state?.toTaskState()
        if(state != null) {
            this.tvTaskState.text =
                    this.getString(state.localizationStringId)
            when(state) {
                TaskState.NewUndistributed -> {
                    this.bttnReserveTask.visibility = View.VISIBLE
                    this.bttnReserveTask.setOnClickListener {
                        this.performTaskAction {
                            this.taskRepository.reserveTask(it)
                        }
                    }
                }
                TaskState.New -> {
                    this.bttnTakeIntoWorkTask.visibility = View.VISIBLE
                    this.bttnTakeIntoWorkTask.setOnClickListener {
                        this.performTaskAction {
                            this.taskRepository.takeTaskToWork(it)
                        }
                    }
                }
                TaskState.InWork -> {
                    this.bttnPerformTask.visibility = View.VISIBLE
                    this.bttnPerformTask.setOnClickListener {
                        this.performTaskAction {
                            this.taskRepository.performTask(it)
                        }
                    }
                    this.bttnCancelTask.visibility = View.VISIBLE
                    this.bttnCancelTask.setOnClickListener {
                        this.performTaskAction {
                            this.taskRepository.cancelTask(it)
                        }
                    }
                }
                else -> {}
            }
        }
    }

    private fun performTaskAction(action: ((taskId: UUID)-> TaskModel?)) {
        this.setProcessingState()
        invokeAsync({
            action(this@TaskFragment.taskId)
        }, {
            if(it != null) {
                this@TaskFragment.navigationController.closeCurrentFragment()
            } else {
                Toast
                        .makeText(
                                this@TaskFragment.activity,
                                "someError",
                                Toast.LENGTH_LONG)
                        .show()
            }
            this@TaskFragment.setProcessingState(false)
        })
    }

    private fun setProcessingState(processing: Boolean = true){
        this.bttnReserveTask.isEnabled = !processing
        this.bttnTakeIntoWorkTask.isEnabled = !processing
        this.bttnPerformTask.isEnabled = !processing
        this.bttnCancelTask.isEnabled = !processing
    }

    companion object {
        fun create(taskId: UUID): TaskFragment {
            val fragment = TaskFragment()
            fragment.taskId = taskId
            return fragment
        }
    }
}
