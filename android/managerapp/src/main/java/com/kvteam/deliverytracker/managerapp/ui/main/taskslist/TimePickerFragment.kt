package com.kvteam.deliverytracker.managerapp.ui.main.taskslist

import android.app.Dialog
import android.app.DialogFragment
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.TimePicker
import org.joda.time.DateTime

class TimePickerFragment : DialogFragment(), TimePickerDialog.OnTimeSetListener {
    private lateinit var callback: (DateTime) -> Unit
    private lateinit var startingTime: DateTime

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = TimePickerDialog(
                activity,
                this,
                startingTime.hourOfDay,
                startingTime.minuteOfHour,
                true)
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