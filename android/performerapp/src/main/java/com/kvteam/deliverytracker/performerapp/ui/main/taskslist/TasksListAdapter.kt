package com.kvteam.deliverytracker.performerapp.ui.main.taskslist

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kvteam.deliverytracker.core.models.TaskModel
import com.kvteam.deliverytracker.core.tasks.TaskState
import com.kvteam.deliverytracker.core.tasks.toTaskState
import com.kvteam.deliverytracker.performerapp.R
import kotlinx.android.synthetic.main.fragment_tasks_item.view.*

class TasksListAdapter(
        var onTaskClick: ((task: TaskModel) -> Unit)?): RecyclerView.Adapter<TasksListAdapter.ViewHolder>() {

    class ViewHolder(v: View): RecyclerView.ViewHolder(v) {
        val tvNumber = v.tvTaskItemNumber!!
        val tvState = v.tvTaskItemState!!
        val llRowLayout = v.llTaskRowLayout!!
    }

    val items = mutableListOf<TaskModel>()

    override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int): TasksListAdapter.ViewHolder {
        val view = LayoutInflater
                .from(parent.context)
                .inflate(
                        R.layout.fragment_tasks_item,
                        parent,
                        false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val task = items[position]
        val state = task.state?.toTaskState()
        holder.tvNumber.text = task.number
        holder.tvState.text = task.state

        holder.llRowLayout.setBackgroundResource(when(state) {
            TaskState.NewUndistributed -> R.color.taskNewUndistributedColor
            TaskState.New -> R.color.taskNewColor
            TaskState.InWork -> R.color.taskInWorkColor
            TaskState.Performed -> R.color.taskPerformedColor
            TaskState.Cancelled -> R.color.taskCancelledColor
            TaskState.CancelledByManager -> R.color.taskCancelledByManagerColor
            else -> R.color.transparent
        })
        holder.llRowLayout.setOnClickListener { onTaskClick?.invoke(task) }
    }

    override fun getItemCount(): Int = items.size
}

