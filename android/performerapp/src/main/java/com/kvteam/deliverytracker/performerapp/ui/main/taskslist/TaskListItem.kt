package com.kvteam.deliverytracker.performerapp.ui.main.taskslist

import android.support.v7.widget.RecyclerView
import android.view.View
import com.kvteam.deliverytracker.core.common.ILocalizationManager
import com.kvteam.deliverytracker.core.models.TaskInfo
import com.kvteam.deliverytracker.core.ui.BaseListHeader
import com.kvteam.deliverytracker.core.ui.BaseListItem
import com.kvteam.deliverytracker.performerapp.R
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.IFlexible
import kotlinx.android.synthetic.main.fragment_task_list_item.view.*

class TaskListItem(
        val task: TaskInfo,
        header: BaseListHeader,
        private val lm: ILocalizationManager)
    : BaseListItem<TaskInfo, TaskListItem.TasksListViewHolder>(task, header) {
    override val key: String
        get() = task.id!!.toString()

    override fun createViewHolder(
            view: View,
            adapter: FlexibleAdapter<IFlexible<RecyclerView.ViewHolder>>?): TasksListViewHolder {
        return TasksListViewHolder(view, adapter)
    }

    override fun bindViewHolder(
            adapter: FlexibleAdapter<IFlexible<RecyclerView.ViewHolder>>?,
            holder: TasksListViewHolder,
            position: Int,
            payloads: MutableList<Any>?) {
        holder.tvTaskCaption.text = lm.getString(task.taskStateCaption!!)
        holder.tvTaskNumber.text = task.taskNumber
    }

    open class TasksListViewHolder(view: View, adapter: FlexibleAdapter<out IFlexible<*>>?) : BaseListItem.BaseListViewHolder(view, adapter) {
        override val layoutID: Int
            get() = R.layout.fragment_task_list_item

        val tvTaskCaption = view.tvTaskCaption!!
        val tvTaskNumber = view.tvTaskNumber!!
    }
}

