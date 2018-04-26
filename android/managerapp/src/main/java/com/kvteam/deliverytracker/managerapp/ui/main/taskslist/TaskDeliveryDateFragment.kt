package com.kvteam.deliverytracker.managerapp.ui.main.taskslist

import android.app.DatePickerDialog
import android.app.Dialog
import android.app.DialogFragment
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.TimePicker
import com.kvteam.deliverytracker.core.async.launchUI
import com.kvteam.deliverytracker.core.dataprovider.base.DataProvider
import com.kvteam.deliverytracker.core.dataprovider.base.DataProviderGetMode
import com.kvteam.deliverytracker.managerapp.R
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_task_delivery_date.*
import kotlinx.android.synthetic.main.fragment_task_delivery_date.view.*
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import org.joda.time.Duration
import org.joda.time.Interval
import javax.inject.Inject



class TaskDeliveryDateFragment : BaseTaskPageFragment() {
    @Inject
    lateinit var dp: DataProvider

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
        view.spinnerDeliveryDate.addOnItemClickListener { adapterView, view, i, l ->
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
        view.spinnerDeliveryTime.addOnItemClickListener { adapterView, view, i, l ->
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

    // SECTION: DatePicker --START--

    class DatePickerFragment : DialogFragment(), DatePickerDialog.OnDateSetListener {
        private lateinit var callback: (DateTime) -> Unit
        private lateinit var startingDate: DateTime

        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            val dialog = DatePickerDialog(activity, this, startingDate.year, startingDate.minusMonths(1).monthOfYear, startingDate.dayOfMonth)
            dialog.datePicker.minDate = DateTime.now().millis
            return dialog
        }

        override fun onDateSet(view: DatePicker, year: Int, month: Int, day: Int) {
            callback(DateTime(year, month + 1, day, 0, 0))
        }

        fun setStartDate(date: DateTime?) {
            startingDate = date ?: DateTime.now()
        }

        fun setOnDateSelectCallback(cb: (DateTime) -> Unit) {
            callback = cb
        }
    }

    private val DATE_TODAY = "Today"
    private val DATE_TOMORROW = "Tomorrow"
    private val DATE_CUSTOM = "Custom date"

    private val deliveryDateTypes = arrayListOf<DeliveryDateTypeItem>(
            DeliveryDateTypeItem(DATE_TODAY, DateTime.now()),
            DeliveryDateTypeItem(DATE_TOMORROW, DateTime.now().plusDays(1)),
            DeliveryDateTypeItem(DATE_CUSTOM, null)
    )

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

    // SECTION: DatePicker --END--

    // SECTION: TimePicker --START--

    private val TIME_MORNING = "09:00 - 12:00"
    private val TIME_AFTERNOON = "12:00 - 18:00"
    private val TIME_EVENING = "18:00 - 22:00"
    private val TIME_CUSTOM = "Exact time"


    private val deliveryTimeTypes = arrayListOf<DeliveryTimeTypeItem>(
                    DeliveryTimeTypeItem(
                            TIME_MORNING,
                            DateTime.now().withTime(9, 0, 0, 0),
                            DateTime.now().withTime(11, 59, 59, 999)),
                    DeliveryTimeTypeItem(
                            TIME_AFTERNOON,
                            DateTime.now().withTime(12, 0, 0, 0),
                            DateTime.now().withTime(17, 59, 59, 999)),
                    DeliveryTimeTypeItem(
                            TIME_EVENING,
                            DateTime.now().withTime(18, 0, 0, 0),
                            DateTime.now().withTime(21, 59, 59, 999)),
                    DeliveryTimeTypeItem(
                            TIME_CUSTOM,
                            null,
                            null)
    )

    class TimePickerFragment : DialogFragment(), TimePickerDialog.OnTimeSetListener {
        private lateinit var callback: (DateTime) -> Unit
        private lateinit var startingTime: DateTime

        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            val dialog = TimePickerDialog(activity, this, startingTime.hourOfDay, startingTime.minuteOfHour, true)
            return dialog
        }

        fun setStartTime(date: DateTime?) {
            startingTime = date ?: DateTime.now()
        }

        fun setOnTimeSelectCallback(cb: (DateTime) -> Unit) {
            callback = cb
        }

        override fun onTimeSet(view: TimePicker, hourOfDay: Int, minute: Int) {
            callback(DateTime.now().withTime(hourOfDay, minute, 0, 0))
        }
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


    // SECTION: TimePicker --END--


    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidSupportInjection.inject(this)
        super.onCreate(savedInstanceState)
    }
}
