package com.kvteam.deliverytracker.managerapp.ui.main.taskslist

import android.content.Context
import android.graphics.Color
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.View
import com.amulyakhare.textdrawable.TextDrawable
import com.kvteam.deliverytracker.core.async.launchUI
import com.kvteam.deliverytracker.core.common.ILocalizationManager
import com.kvteam.deliverytracker.core.dataprovider.DataProvider
import com.kvteam.deliverytracker.core.dataprovider.DataProviderGetMode
import com.kvteam.deliverytracker.core.models.TaskInfo
import com.kvteam.deliverytracker.core.tasks.getTaskState
import com.kvteam.deliverytracker.core.tasks.getTaskStateCaption
import com.kvteam.deliverytracker.core.ui.BaseListHeader
import com.kvteam.deliverytracker.core.ui.BaseListItem
import com.kvteam.deliverytracker.managerapp.R
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.IFlexible
import kotlinx.android.synthetic.main.fragment_task_list_item.view.*

class TaskListItem(
        val task: TaskInfo,
        header: BaseListHeader,
        private val lm: ILocalizationManager,
        private val dp: DataProvider,
        private val activityContext: Context)
    : BaseListItem<TaskInfo, TaskListItem.TasksListViewHolder>(task, header) {
    override val key: String
        get() = task.id!!.toString()

    override fun createViewHolder(view: View?, adapter: FlexibleAdapter<IFlexible<RecyclerView.ViewHolder>>?): TasksListViewHolder {
        return TasksListViewHolder(view!!, adapter);
    }

    override fun bindViewHolder(
            adapter: FlexibleAdapter<IFlexible<RecyclerView.ViewHolder>>?,
            holder: TasksListViewHolder?,
            position: Int,
            payloads: MutableList<Any>?) = launchUI {
        holder!!.tvTaskNumber.text = task.taskNumber
        holder.tvTaskState.text = lm.getString(task.taskStateCaption!!)
        holder.tvTaskState.setTextColor(Color.WHITE)
        holder.tvTaskState.setBackgroundColor(ContextCompat.getColor(activityContext, task.getTaskState()!!.color))
        holder.tvDeliveryDate.text = task.deliveryFrom?.toString("dd.MM")
        holder.tvTaskAddress.text = "Bolshaya Pochtovaya st. 18/20, 30"
        holder.tvTaskState.text = task.getTaskStateCaption(lm)

        if (task.performerId != null) {
            val performer = dp.users.getAsync(task.performerId!!, DataProviderGetMode.PREFER_CACHE).entry
            val text = StringBuilder(4)
            if (performer.name?.isNotBlank() == true) {
                text.append(performer.name!![0].toString())
            }
            if (performer.surname?.isNotBlank() == true) {
                text.append(performer.surname!![0].toString())
            }

            val materialAvatarDefault = TextDrawable.builder()
                    .buildRound(text.toString(), Color.LTGRAY)

            holder!!.ivUserAvatar.setImageDrawable(materialAvatarDefault)
            holder.tvName.text = performer.name
            holder.tvSurname.text = performer.surname
        } else {
            holder!!.ivUserAvatar.visibility = View.GONE
            holder.tvName.text = activityContext.getString(R.string.NoTaskPerformer)
        }
    }

    open class TasksListViewHolder(view: View, adapter: FlexibleAdapter<out IFlexible<*>>?) : BaseListItem.BaseListViewHolder(view, adapter) {
        override val layoutID: Int
            get() = R.layout.fragment_task_list_item

        val tvTaskNumber = view.tvTaskNumber!!
        val tvTaskState = view.tvTaskState!!
        val tvTaskAddress = view.tvTaskAddress!!
        val tvDeliveryDate = view.tvDeliveryDate!!
        val tvName = view.tvName!!
        val tvSurname = view.tvSurname!!
        val ivUserAvatar = view.ivUserAvatar!!
    }
}

