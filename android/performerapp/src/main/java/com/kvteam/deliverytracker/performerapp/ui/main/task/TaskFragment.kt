package com.kvteam.deliverytracker.performerapp.ui.main.task

import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.kvteam.deliverytracker.core.async.invokeAsync
import com.kvteam.deliverytracker.core.ui.AutoClearedValue
import com.kvteam.deliverytracker.core.ui.DeliveryTrackerFragment
import com.kvteam.deliverytracker.core.ui.DeliveryTrackerViewModelFactory

import com.kvteam.deliverytracker.performerapp.R
import com.kvteam.deliverytracker.performerapp.databinding.FragmentTaskBinding
import com.kvteam.deliverytracker.performerapp.tasks.ITaskRepository
import com.kvteam.deliverytracker.performerapp.ui.main.NavigationController
import dagger.android.support.AndroidSupportInjection
import java.util.*
import javax.inject.Inject

class TaskFragment : DeliveryTrackerFragment() {
    @Inject
    lateinit var vmFactory: DeliveryTrackerViewModelFactory
    @Inject
    lateinit var taskRepository: ITaskRepository
    @Inject
    lateinit var navigationController: NavigationController

    private lateinit var binding: AutoClearedValue<FragmentTaskBinding>

    lateinit var taskId: UUID
        private set

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidSupportInjection.inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val dataBinding = DataBindingUtil.inflate<FragmentTaskBinding>(
                inflater,
                R.layout.fragment_task,
                container,
                false)
        binding = AutoClearedValue(
                this,
                dataBinding,
                {
                    it?.executePendingBindings()
                    it?.unbind()
                    it?.fragment = null
                    it?.viewModel = null
                })
        return dataBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val viewModel = ViewModelProviders
                .of(this, vmFactory)
                .get(TaskViewModel::class.java)

        binding.value?.viewModel = viewModel
        binding.value?.fragment = this

        invokeAsync({
            taskRepository.getTask(taskId)
        }, {
            binding.value?.viewModel?.task?.set(it)
        })
    }

    fun onReserveButtonClicked(v: View) {
        val taskId = binding.value?.viewModel?.task?.get()?.id
        binding.value?.viewModel?.operationPending?.set(true)
        if(taskId == null) {
            return
        }
        invokeAsync({
            taskRepository.reserveTask(taskId)
        }, {
            if(it != null) {
                navigationController.closeCurrentFragment()
            } else {
                Toast.makeText(activity, "someError", Toast.LENGTH_LONG).show()
            }
            binding.value?.viewModel?.operationPending?.set(false)
        })

    }

    fun onTakeIntoWorkButtonClicked(v: View) {
        val taskId = binding.value?.viewModel?.task?.get()?.id
        binding.value?.viewModel?.operationPending?.set(true)
        if(taskId == null) {
            return
        }
        invokeAsync({
            taskRepository.takeTaskToWork(taskId)
        }, {
            if(it != null) {
                navigationController.closeCurrentFragment()
            } else {
                Toast.makeText(activity, "someError", Toast.LENGTH_LONG).show()
            }
            binding.value?.viewModel?.operationPending?.set(false)
        })
    }

    fun onPerformButtonClicked(v: View) {
        val taskId = binding.value?.viewModel?.task?.get()?.id
        binding.value?.viewModel?.operationPending?.set(true)
        if(taskId == null) {
            return
        }
        invokeAsync({
            taskRepository.performTask(taskId)
        }, {
            if(it != null) {
                navigationController.closeCurrentFragment()
            } else {
                Toast.makeText(activity, "someError", Toast.LENGTH_LONG).show()
            }
            binding.value?.viewModel?.operationPending?.set(false)
        })
    }

    fun onCancelButtonClicked(v: View) {
        val taskId = binding.value?.viewModel?.task?.get()?.id
        binding.value?.viewModel?.operationPending?.set(true)
        if(taskId == null) {
            return
        }
        invokeAsync({
            taskRepository.cancelTask(taskId)
        }, {
            if(it != null) {
                navigationController.closeCurrentFragment()
            } else {
                Toast.makeText(activity, "someError", Toast.LENGTH_LONG).show()
            }
            binding.value?.viewModel?.operationPending?.set(false)
        })
    }

    companion object {
        fun create(taskId: UUID): TaskFragment {
            val fragment = TaskFragment()
            fragment.taskId = taskId
            return fragment
        }
    }
}
