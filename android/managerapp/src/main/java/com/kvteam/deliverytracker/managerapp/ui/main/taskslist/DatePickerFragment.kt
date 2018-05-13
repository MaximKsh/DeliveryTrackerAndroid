package com.kvteam.deliverytracker.managerapp.ui.main.taskslist

import android.app.DatePickerDialog
import android.app.Dialog
import android.app.DialogFragment
import android.os.Bundle
import android.widget.DatePicker
import org.joda.time.DateTime

class DatePickerFragment : DialogFragment(), DatePickerDialog.OnDateSetListener {
    private lateinit var callback: (DateTime) -> Unit
    private lateinit var startingDate: DateTime

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = DatePickerDialog(
                activity,
                this,
                startingDate.year,
                startingDate.minusMonths(1).monthOfYear,
                startingDate.dayOfMonth)
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