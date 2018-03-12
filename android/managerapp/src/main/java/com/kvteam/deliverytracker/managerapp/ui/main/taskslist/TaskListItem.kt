package com.kvteam.deliverytracker.managerapp.ui.main.taskslist

import android.graphics.Color
import android.view.View
import com.kvteam.deliverytracker.core.common.ILocalizationManager
import com.kvteam.deliverytracker.core.models.TaskInfo
import com.kvteam.deliverytracker.core.ui.BaseListHeader
import com.kvteam.deliverytracker.core.ui.BaseListItem
import com.kvteam.deliverytracker.managerapp.R
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.IFlexible
import eu.davidea.flexibleadapter.utils.DrawableUtils
import kotlinx.android.synthetic.main.task_list_item.view.*

class TaskListItem(
        val task: TaskInfo,
        header: BaseListHeader,
        private val lm: ILocalizationManager)
    : BaseListItem<TaskInfo, TaskListItem.TasksListViewHolder>(task, header) {
    override val key: String
        get() = task.id!!.toString()

    override fun createViewHolder(view: View, adapter: FlexibleAdapter<out IFlexible<*>>?) : TasksListViewHolder {
        return TasksListViewHolder(view, adapter);
    }

    override fun bindViewHolder(adapter: FlexibleAdapter<out IFlexible<*>>?, holder: TasksListViewHolder, position: Int, payloads: MutableList<Any>?) {
        val context = holder.itemView.context

        val drawable = DrawableUtils.getSelectableBackgroundCompat(
                Color.WHITE, Color.parseColor("#dddddd"),
                DrawableUtils.getColorControlHighlight(context))

        DrawableUtils.setBackgroundCompat(holder.itemView, drawable)

        holder.tvTaskCaption.text = lm.getString(task.taskStateCaption!!)
        holder.tvTaskNumber.text = task.taskNumber
    }

    open class TasksListViewHolder(view: View, adapter: FlexibleAdapter<out IFlexible<*>>?) : BaseListItem.BaseListViewHolder(view, adapter) {
        override val layoutID: Int
            get() = R.layout.task_list_item

        val tvTaskCaption = view.tvTaskCaption!!
        val tvTaskNumber = view.tvTaskNumber!!
    }
}

