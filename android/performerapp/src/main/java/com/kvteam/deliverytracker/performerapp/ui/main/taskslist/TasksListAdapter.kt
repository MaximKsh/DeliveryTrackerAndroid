package com.kvteam.deliverytracker.performerapp.ui.main.taskslist

import android.databinding.DataBindingUtil
import android.view.LayoutInflater
import android.view.ViewGroup
import com.kvteam.deliverytracker.core.models.TaskModel
import com.kvteam.deliverytracker.core.ui.DataBoundListAdapter
import com.kvteam.deliverytracker.performerapp.R
import com.kvteam.deliverytracker.performerapp.databinding.FragmentTasksItemBinding

class TasksListAdapter
    : DataBoundListAdapter<TaskModel, FragmentTasksItemBinding>() {

    override fun createBinding(parent: ViewGroup): FragmentTasksItemBinding {
        return DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.fragment_tasks_item,
                parent,
                false)
    }

    override fun bind(binding: FragmentTasksItemBinding, item: TaskModel) {
        binding.task = item
    }

    override fun areItemsTheSame(oldItem: TaskModel, newItem: TaskModel): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: TaskModel, newItem: TaskModel): Boolean {
        return oldItem.id == newItem.id
                && oldItem.number === newItem.number
                && oldItem.details === newItem.details
                && oldItem.shippingDesc === newItem.shippingDesc
                && oldItem.address === newItem.address
                && oldItem.state === newItem.state
    }
}