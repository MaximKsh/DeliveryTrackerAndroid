package com.kvteam.deliverytracker.managerapp.ui.main.taskslist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kvteam.deliverytracker.core.async.launchUI
import com.kvteam.deliverytracker.core.dataprovider.base.DataProviderGetMode
import com.kvteam.deliverytracker.managerapp.R
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_task_delivery_date.*
import kotlinx.android.synthetic.main.fragment_task_delivery_date.view.*
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import org.joda.time.Duration
import org.joda.time.Interval


class TaskDeliveryDateFragment : BaseTaskPageFragment() {

    private val dateToday by lazy { lm.getString(R.string.ManagerApp_EditTaskFragment_Today) }
    private val dateTomorrow by lazy { lm.getString(R.string.ManagerApp_EditTaskFragment_Tomorrow) }
    private val dateCustom  by lazy { lm.getString(R.string.ManagerApp_EditTaskFragment_ExactDate) }

    private lateinit var deliveryDateTypes: List<DeliveryDateTypeItem>

    private val timeMorning by lazy { lm.getString(R.string.ManagerApp_EditTaskFragment_Morning) }
    private val timeAfternoon by lazy { lm.getString(R.string.ManagerApp_EditTaskFragment_Afternoon) }
    private val timeEvening by lazy { lm.getString(R.string.ManagerApp_EditTaskFragment_Evening) }
    private val timeCustom by lazy { lm.getString(R.string.ManagerApp_EditTaskFragment_ExactTime) }


    private lateinit var deliveryTimeTypes: List<DeliveryTimeTypeItem>


    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidSupportInjection.inject(this)
        super.onCreate(savedInstanceState)

        deliveryDateTypes = listOf(
                DeliveryDateTypeItem(dateToday, DateTime.now()),
                DeliveryDateTypeItem(dateTomorrow, DateTime.now().plusDays(1)),
                DeliveryDateTypeItem(dateCustom, null)
        )
        deliveryTimeTypes = listOf(
                DeliveryTimeTypeItem(
                        timeMorning,
                        DateTime.now().withTime(9, 0, 0, 0),
                        DateTime.now().withTime(11, 59, 59, 999)),
                DeliveryTimeTypeItem(
                        timeAfternoon,
                        DateTime.now().withTime(12, 0, 0, 0),
                        DateTime.now().withTime(17, 59, 59, 999)),
                DeliveryTimeTypeItem(
                        timeEvening,
                        DateTime.now().withTime(18, 0, 0, 0),
                        DateTime.now().withTime(21, 59, 59, 999)),
                DeliveryTimeTypeItem(
                        timeCustom,
                        null,
                        null)
        )
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_task_delivery_date, container, false) as ViewGroup

        val task = dp.taskInfos.get(taskId, DataProviderGetMode.DIRTY).entry

        view.tvCustomDate.setOnClickListener { _ ->
            val datePickerFragment = DatePickerFragment()
            datePickerFragment.setOnDateSelectCallback({ date: DateTime -> updateCustomDateView(date) })
            val selectedDate = deliveryDateTypes[2].selectedDateTime
            datePickerFragment.setStartDate(selectedDate)
            datePickerFragment.show(activity!!.fragmentManager, "datePicker")
        }

        view.tvCustomTime.setOnClickListener { _ ->
            val timePickerFragment = TimePickerFragment()
            timePickerFragment.setOnTimeSelectCallback({ date: DateTime -> updateCustomTimeView(date) })
            val selectedTime = deliveryTimeTypes[3].fromTime
            timePickerFragment.setStartTime(selectedTime)
            timePickerFragment.show(activity!!.fragmentManager, "timePicker")
        }

        val stringDates = deliveryDateTypes.map { it.name }
        view.spinnerDeliveryDate.attachDataSource(stringDates)
        var oldSelectedDateTypeIndex = -1
        view.spinnerDeliveryDate.addOnItemClickListener { _, _, i, _ ->
            if ((oldSelectedDateTypeIndex == i && i == deliveryDateTypes.size - 1) || deliveryDateTypes[i].selectedDateTime == null) {
                oldSelectedDateTypeIndex = i
                val datePickerFragment = DatePickerFragment()
                datePickerFragment.setOnDateSelectCallback({ date: DateTime -> updateCustomDateView(date) })
                val selectedDate = deliveryDateTypes[i].selectedDateTime
                datePickerFragment.setStartDate(selectedDate)
                datePickerFragment.show(activity!!.fragmentManager, "datePicker")
            } else {
                hideDateChip()
                setTaskDate(deliveryDateTypes[i].selectedDateTime!!)
            }
        }

        val stringTimes = deliveryTimeTypes.map { it.name }
        view.spinnerDeliveryTime.attachDataSource(stringTimes)
        var oldSelectedTimeTypeIndex = -1
        view.spinnerDeliveryTime.addOnItemClickListener { _, _, i, _ ->
            if ((oldSelectedTimeTypeIndex == i && i == deliveryTimeTypes.size - 1) || deliveryTimeTypes[i].fromTime == null) {
                oldSelectedTimeTypeIndex = i
                val timePickerFragment = TimePickerFragment()
                timePickerFragment.setOnTimeSelectCallback({ date: DateTime -> updateCustomTimeView(date) })
                val selectedTime = deliveryTimeTypes[i].fromTime
                timePickerFragment.setStartTime(selectedTime)
                timePickerFragment.show(activity!!.fragmentManager, "timePicker")
            } else {
                hideTimeChip()
                setTaskTime(deliveryTimeTypes[i].fromTime!!, deliveryTimeTypes[i].toTime!!)
            }
        }

        if (task.deliveryFrom != null) {
            val selectedDateIndex = getDateIndex(task.deliveryFrom!!)
            view.spinnerDeliveryDate.selectedIndex = selectedDateIndex
            if (selectedDateIndex == 2) {
                showDateChip((task.deliveryFrom as DateTime).toString("dd.MM"), view)
            }

            val selectedTimeIndex = getTimeIntervalIndex(task.deliveryFrom!!, task.deliveryTo!!)
            view.spinnerDeliveryTime.selectedIndex = selectedTimeIndex
            if (selectedTimeIndex == deliveryTimeTypes.size - 1) {
                showTimeChip(task.deliveryFrom!!.toString("HH:mm"), view)
            }
        } else {
            setTaskDate(deliveryDateTypes[0].selectedDateTime!!)
            setTaskTime(deliveryTimeTypes[0].fromTime!!, deliveryTimeTypes[0].toTime!!)
        }

        view.spinnerDeliveryDate.setPadding(15, 10, 0, 10)
        view.spinnerDeliveryTime.setPadding(15, 10, 0, 10)

        return view
    }

    private fun getDateIndex (date: DateTime): Int {
        val duration = Duration(DateTime.now(DateTimeZone.UTC), date)
        return when (duration.standardDays.toInt()) {
            0 -> 0
            1 -> 1
            else -> 2
        }
    }

    private fun getTimeIntervalIndex (dateFrom: DateTime, dateTo: DateTime): Int {
        val dateFromNormalized = dateFrom.withDate(DateTime.now().toLocalDate())
        val dateToNormalized = dateTo.withDate(DateTime.now().toLocalDate())
        if (dateFrom.isEqual(dateTo)) {
            return deliveryTimeTypes.size - 1
        }
        for (i in 0 until deliveryTimeTypes.size) {
            val interval  = Interval(deliveryTimeTypes[i].fromTime, deliveryTimeTypes[i].toTime)
            if (interval.contains(dateFromNormalized) && interval.contains(dateToNormalized)) {
                return i
            }
        }
        return deliveryTimeTypes.size - 1
    }

    private fun setTaskDate(date: DateTime) = launchUI {
        val task = dp.taskInfos.getAsync(taskId, DataProviderGetMode.DIRTY).entry
        val dateReal = task.deliveryFrom ?: DateTime.now()
        task.deliveryFrom = dateReal.withDate(date.toLocalDate())
        task.deliveryTo = dateReal.withDate(date.toLocalDate())
    }

    private fun updateCustomDateView(date: DateTime) {
        setTaskDate(date)
        showDateChip(date.toString("dd.MM"))
    }

    private fun showDateChip(dateString: String, view: View = this.view!!) {
        view.tvCustomDate.visibility = View.VISIBLE
        view.tvCustomDate.text = dateString
    }

    private fun hideDateChip() {
        tvCustomDate.visibility = View.GONE
    }

    private fun setTaskTime(fromTime: DateTime, toTime: DateTime) = launchUI {
        val task = dp.taskInfos.getAsync(taskId, DataProviderGetMode.DIRTY).entry
        val timeFromReal = task.deliveryFrom ?: DateTime.now()
        val timeToReal = task.deliveryTo ?: DateTime.now()
        task.deliveryFrom = timeFromReal.withTime(fromTime.toLocalTime())
        task.deliveryTo = timeToReal.withTime(toTime.toLocalTime())
    }

    private fun updateCustomTimeView(date: DateTime) {
        setTaskTime(date, date)
        showTimeChip(date.toString("HH:mm"))
    }

    private fun showTimeChip(timeString: String, view: View = this.view!!) {
        view.tvCustomTime.visibility = View.VISIBLE
        view.tvCustomTime.text = timeString
    }

    private fun hideTimeChip() {
        tvCustomTime.visibility = View.GONE
    }
}
