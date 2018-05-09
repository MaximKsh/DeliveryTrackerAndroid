package com.kvteam.deliverytracker.managerapp.ui.main.userslist

import android.graphics.Color
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kvteam.deliverytracker.core.async.launchUI
import com.kvteam.deliverytracker.core.dataprovider.base.DataProvider
import com.kvteam.deliverytracker.core.dataprovider.base.DataProviderGetMode
import com.kvteam.deliverytracker.core.roles.Role
import com.kvteam.deliverytracker.core.ui.DeliveryTrackerFragment
import com.kvteam.deliverytracker.core.webservice.IViewWebservice
import com.kvteam.deliverytracker.managerapp.R
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_user_statistics.*
import java.util.*
import javax.inject.Inject
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.kvteam.deliverytracker.core.common.*
import com.kvteam.deliverytracker.core.tasks.TaskState
import com.github.mikephil.charting.components.XAxis.XAxisPosition
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.utils.ColorTemplate
import com.kvteam.deliverytracker.core.models.TaskStatisticsItem
import org.joda.time.DateTime
import org.joda.time.DateTimeZone


class UserStatsFragment: DeliveryTrackerFragment() {
    private class PerformerStateValueFormatter (private val lm: ILocalizationManager) : IAxisValueFormatter {
        override fun getFormattedValue(value: Float, axis: AxisBase?): String {
            return when(value) {
                0f -> lm.getString(TaskState.Queue.stateCaption)
                1f -> lm.getString(TaskState.Waiting.stateCaption).replace(' ', '\n')
                2f -> lm.getString(TaskState.IntoWork.stateCaption)
                3f -> lm.getString(TaskState.Delivered.stateCaption)
                else -> EMPTY_STRING
            }
        }
    }

    private class StateValueFormatter (private val lm: ILocalizationManager) : IAxisValueFormatter {
        override fun getFormattedValue(value: Float, axis: AxisBase?): String {
            return when(value) {
                0f -> lm.getString(TaskState.Preparing.stateCaption)
                1f -> lm.getString(TaskState.Queue.stateCaption)
                2f -> lm.getString(TaskState.Waiting.stateCaption).replace(' ', '\n')
                3f -> lm.getString(TaskState.IntoWork.stateCaption)
                4f -> lm.getString(TaskState.Delivered.stateCaption)
                5f -> lm.getString(TaskState.Complete.stateCaption)
                6f -> lm.getString(TaskState.Revoked.stateCaption)
                else -> EMPTY_STRING
            }
        }
    }

    private class DateValueFormatter (private val max: Int) : IAxisValueFormatter {
        override fun getFormattedValue(value: Float, axis: AxisBase?): String {
            val diff = max - value - 1
            return DateTime.now(DateTimeZone.UTC).minusDays(diff.toInt()).toString("dd.MM")
        }
    }

    @Inject
    lateinit var lm: ILocalizationManager

    @Inject
    lateinit var dp: DataProvider

    private val userIdKey = "userId"
    private var userId
        get() = arguments?.getSerializable(userIdKey)!! as UUID
        set(value) = arguments?.putSerializable(userIdKey, value)!!

    private val userRoleKey = "userRole"
    private var userRole
        get() = arguments?.getSerializable(userRoleKey)!! as Role
        set(value) = arguments?.putSerializable(userRoleKey, value)!!

    fun setUser(id: UUID, role: Role) {
        this.userId = id
        this.userRole = role
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidSupportInjection.inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_user_statistics, container, false) as ViewGroup
        return rootView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) = launchUI {
        super.onActivityCreated(savedInstanceState)
        val actual = dp.statistics.getAsync(userId, userRole, ActualTasksStateDistributionView, DataProviderGetMode.PREFER_CACHE)
        val performance = dp.statistics.getAsync(userId, userRole, PerformanceStatisticsView, DataProviderGetMode.PREFER_CACHE)

        if (actual != null && actual.isNotEmpty()) {
            initActual(actual[0], userRole)
        }

        if (performance != null && performance.isNotEmpty()) {
            initPerformance(performance, userRole)
        }
    }


    private fun initActual(actual: TaskStatisticsItem, role: Role) {
        val entries = ArrayList<BarEntry>()
        if (role == Role.Performer) {
            entries.add(BarEntry(0f, actual.queue?.toFloat() ?: 0f))
            entries.add(BarEntry(1f, actual.waiting?.toFloat() ?: 0f))
            entries.add(BarEntry(2f, actual.intoWork?.toFloat() ?: 0f))
            entries.add(BarEntry(3f, actual.delivered?.toFloat() ?: 0f))
        } else {
            entries.add(BarEntry(0f, actual.preparing?.toFloat() ?: 0f))
            entries.add(BarEntry(1f, actual.queue?.toFloat() ?: 0f))
            entries.add(BarEntry(2f, actual.waiting?.toFloat() ?: 0f))
            entries.add(BarEntry(3f, actual.intoWork?.toFloat() ?: 0f))
            entries.add(BarEntry(4f, actual.delivered?.toFloat() ?: 0f))
            entries.add(BarEntry(5f, actual.complete?.toFloat() ?: 0f))
            entries.add(BarEntry(6f, actual.revoked?.toFloat() ?: 0f))
        }
        val set = BarDataSet(entries, "ActualTasks")
        set.color = ContextCompat.getColor(context!!, R.color.colorPrimary)
        val data = BarData(set)
        bcActualTasks.data = data
        bcActualTasks.setFitBars(true) // make the x-axis fit exactly all bars
        bcActualTasks.legend?.isEnabled = false
        bcActualTasks.description.isEnabled = false

        val xAxis = bcActualTasks.xAxis
        xAxis.position = XAxisPosition.BOTTOM
        xAxis.granularity = 1f
        xAxis.labelCount = 7
        xAxis.valueFormatter = if (role == Role.Performer) {
            PerformerStateValueFormatter(lm)
        } else {
            StateValueFormatter(lm)
        }

        bcActualTasks.axisLeft.isEnabled = false
        bcActualTasks.axisRight.isEnabled = false

        bcActualTasks.invalidate()
    }

    private fun initPerformance(performance: List<TaskStatisticsItem>, role: Role) {
        val data = LineData()

        if (role == Role.Performer) {
            val entriesCompleted = performance.mapIndexed { index, taskStatisticsItem ->
                Entry(index.toFloat(), taskStatisticsItem.completed?.toFloat() ?: 0f)
            }
            val completedSet = LineDataSet(entriesCompleted, lm.getString(R.string.ManagerApp_UserDetails_DeliveredTasksLabel))
            completedSet.color = ContextCompat.getColor(context!!, R.color.colorPrimary)
            completedSet.lineWidth = 2f
            data.addDataSet(completedSet)
        } else {
            val entriesCreated = performance.mapIndexed { index, taskStatisticsItem ->
                Entry(index.toFloat(), taskStatisticsItem.created?.toFloat() ?: 0f)
            }
            val createdSet = LineDataSet(entriesCreated, lm.getString(R.string.ManagerApp_UserDetails_CreatedTasksLabel))
            createdSet.color = ContextCompat.getColor(context!!, R.color.colorPrimary)
            createdSet.lineWidth = 2f

            val entriesCompleted = performance.mapIndexed { index, taskStatisticsItem ->
                Entry(index.toFloat(), taskStatisticsItem.completed?.toFloat() ?: 0f)
            }
            val completedSet = LineDataSet(entriesCompleted, lm.getString(R.string.ManagerApp_UserDetails_CompletedTasksLabel))
            completedSet.color = ColorTemplate.getHoloBlue()
            completedSet.lineWidth = 2f

            data.addDataSet(createdSet)
            data.addDataSet(completedSet)
        }

        lcPerformance.data = data
        val xAxis = lcPerformance.xAxis
        xAxis.position = XAxisPosition.BOTTOM
        xAxis.granularity = 1f
        xAxis.labelCount = 8
        xAxis.valueFormatter = DateValueFormatter(8)

        lcPerformance.description.isEnabled = false
        lcPerformance.axisRight.isEnabled = false
        lcPerformance.axisLeft.mAxisMinimum = 0f
        lcPerformance.axisLeft.granularity = 1f

        lcPerformance.invalidate()
    }
}