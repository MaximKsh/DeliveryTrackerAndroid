package com.kvteam.deliverytracker.performerapp.ui.main.dayroute

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.kvteam.deliverytracker.performerapp.R
import kotlinx.android.synthetic.main.stepper_task_item.view.*
import android.os.Parcelable
import android.util.Log


class TaskStepperItemView : LinearLayout {
    constructor(context: Context) : super(context) {
        initialize(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initialize(context)
    }

    constructor(
            context: Context,
            attrs: AttributeSet?,
            defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initialize(context)
    }

    private fun initialize(context: Context) {
        clipChildren = true
        orientation = LinearLayout.VERTICAL

        LayoutInflater.from(context).inflate(R.layout.stepper_task_item, this, true)
    }
}