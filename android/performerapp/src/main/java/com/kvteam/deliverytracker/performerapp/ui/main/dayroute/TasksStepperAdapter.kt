package com.kvteam.deliverytracker.performerapp.ui.main.dayroute

import android.content.Context
import android.view.View
import com.google.android.gms.maps.model.LatLng
import com.kvteam.deliverytracker.core.common.MapsAdapter
import com.kvteam.deliverytracker.core.models.TaskInfo
import com.liefery.android.vertical_stepper_view.VerticalStepperAdapter
import com.liefery.android.vertical_stepper_view.VerticalStepperItemView.STATE_ACTIVE
import kotlinx.android.synthetic.main.stepper_task_item.view.*
import java.util.*


class TasksStepperContainer(
        private val goToTaskDetailsCallback: (UUID) -> Unit,
        private val goToMarkerCallback: (LatLng) -> Unit,
        private val tasks: List<TaskStepperInfo>,
        private val context: Context) {
    inner class TasksStepperAdapter(context: Context) : VerticalStepperAdapter(context) {
        override fun getTitle(position: Int): CharSequence {
            return ""
        }

        override fun getSummary(position: Int): CharSequence? {
            return null
        }

        override fun isEditable(position: Int): Boolean {
            return true
        }

        override fun getCount(): Int {
            return tasks.size
        }

        override fun getItem(position: Int): Void? {
            return null
        }

        override fun getState(position: Int): Int {
            return STATE_ACTIVE
        }

        override fun onCreateContentView(context: Context, position: Int): View {
            val content = TaskStepperItemView(context)
            content.stepperTaskName.text = tasks[position].name
            content.stepperTaskDescription.text = tasks[position].description ?: "Нет описания"
            content.stepperTaskAddress.text = tasks[position].address
            content.stepperTaskPredictedTime.text = tasks[position].predictedTime.toString("HH:mm")
            content.stepperTaskGoToMarker.setOnClickListener {
                goToMarkerCallback(tasks[position].latLng)
            }
            content.stepperTaskGoToDetails.setOnClickListener { goToTaskDetailsCallback(tasks[position].taskId) }
            return content
        }
    }

    fun getAdapter(): TasksStepperAdapter {
        return TasksStepperAdapter(context)
    }
}

