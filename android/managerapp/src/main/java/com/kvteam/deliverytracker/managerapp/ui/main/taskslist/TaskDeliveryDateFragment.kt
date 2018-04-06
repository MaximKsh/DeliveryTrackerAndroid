package com.kvteam.deliverytracker.managerapp.ui.main.taskslist

import android.app.DatePickerDialog
import android.app.Dialog
import android.app.DialogFragment
import android.app.TimePickerDialog
import android.graphics.pdf.PdfDocument
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.TimePicker
import com.kvteam.deliverytracker.core.async.launchUI
import com.kvteam.deliverytracker.core.dataprovider.DataProvider
import com.kvteam.deliverytracker.core.dataprovider.DataProviderGetMode
import com.kvteam.deliverytracker.core.ui.DeliveryTrackerFragment
import com.kvteam.deliverytracker.core.ui.dropdownselect.DropdownSelect
import com.kvteam.deliverytracker.core.ui.dropdownselect.DropdownSelectItem
import com.kvteam.deliverytracker.managerapp.R
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_task_delivery_date.*
import kotlinx.android.synthetic.main.fragment_task_delivery_date.view.*
import kotlinx.android.synthetic.main.fragment_task_receipt_at.view.*
import org.joda.time.DateTime
import java.util.*
import javax.inject.Inject

data class DeliveryDateTypeItem(
        var name: String,
        var selectedDateTime: DateTime?
)

data class DeliveryTimeTypeItem(
        var name: String,
        var fromTime: DateTime?,
        var toTime: DateTime?
)

class TaskDeliveryDateFragment : PageFragment() {
    @Inject
    lateinit var dp: DataProvider

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_task_delivery_date, container, false) as ViewGroup

        view.tvCustomDate.setOnClickListener { _ ->
            val datePickerFragment = DatePickerFragment()
            datePickerFragment.setOnDateSelectCallback({ date: DateTime -> updateCustomDateView(date, 2) })
            val selectedDate = deliveryDateTypes[2].selectedDateTime
            datePickerFragment.setStartDate(selectedDate)
            datePickerFragment.show(activity!!.fragmentManager, "datePicker")
        }

        view.tvCustomTime.setOnClickListener { _ ->
            val timePickerFragment = TimePickerFragment()
            timePickerFragment.setOnTimeSelectCallback({ date: DateTime -> updateCustomTimeView(date, 3) })
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
                datePickerFragment.setOnDateSelectCallback({ date: DateTime -> updateCustomDateView(date, i) })
                val selectedDate = deliveryDateTypes[i].selectedDateTime
                datePickerFragment.setStartDate(selectedDate)
                datePickerFragment.show(activity!!.fragmentManager, "datePicker")
            } else {
                hideDateChip()
                setTaskDate(deliveryDateTypes[i].selectedDateTime!!)
            }

            selectedDeliveryDateTypeIndex = i
        }

        val stringTimes = deliveryTimeTypes.map { it.name }
        view.spinnerDeliveryTime.attachDataSource(stringTimes)
        var oldSelectedTimeTypeIndex = -1
        view.spinnerDeliveryTime.addOnItemClickListener { adapterView, view, i, l ->
            if ((oldSelectedTimeTypeIndex == i && i == deliveryTimeTypes.size - 1) || deliveryTimeTypes[i].fromTime == null) {
                oldSelectedTimeTypeIndex = i
                val timePickerFragment = TimePickerFragment()
                timePickerFragment.setOnTimeSelectCallback({ date: DateTime -> updateCustomTimeView(date, i) })
                val selectedTime = deliveryTimeTypes[i].fromTime
                timePickerFragment.setStartTime(selectedTime)
                timePickerFragment.show(activity!!.fragmentManager, "timePicker")
            } else {
                hideTimeChip()
                setTaskTime(deliveryTimeTypes[i].fromTime!!, deliveryTimeTypes[i].toTime!!)
            }

            selectedDeliveryTimeTypeIndex = i
        }
        return view
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

    private val selectedDeliveryDateTypeKey = "delivery_date_type"
    private var selectedDeliveryDateTypeIndex: Int?
        get() = arguments?.get(selectedDeliveryDateTypeKey) as Int?
        set(value) = arguments?.putInt(selectedDeliveryDateTypeKey, value!!)!!

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

    private fun updateCustomDateView(date: DateTime, index: Int) {
        val customDateItem = deliveryDateTypes[index]
        customDateItem.name = date.toString("dd.MM")
        customDateItem.selectedDateTime = date
        setTaskDate(date)
        showDateChip(customDateItem.name)
    }

    private fun showDateChip(dateString: String) {
        tvCustomDate.visibility = View.VISIBLE
        tvCustomDate.text = dateString
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

    private val selectedDeliveryTimeTypeKey = "delivery_time_type"
    private var selectedDeliveryTimeTypeIndex: Int?
        get() = arguments?.get(selectedDeliveryTimeTypeKey) as Int?
        set(value) = arguments?.putInt(selectedDeliveryTimeTypeKey, value!!)!!

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

    private fun updateCustomTimeView(date: DateTime, index: Int) {
        val customTimeItem = deliveryTimeTypes[index]
        customTimeItem.name = date.toString("HH:mm")
        customTimeItem.fromTime = date
        customTimeItem.toTime = date
        setTaskTime(date, date)
        showTimeChip(customTimeItem.name)
    }

    private fun showTimeChip(timeString: String) {
        tvCustomTime.visibility = View.VISIBLE
        tvCustomTime.text = timeString
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
