package com.kvteam.deliverytracker.core.ui.tasks

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.Button
import android.widget.RelativeLayout
import com.kvteam.deliverytracker.core.R
import kotlinx.android.synthetic.main.switch_state_view.view.*

class SwitchStateView: RelativeLayout {
    constructor(context: Context) : super(context){
        initializeViews()
    }

    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet) {
        initializeViews()
    }

    constructor(context: Context, attributeSet: AttributeSet, defStyle: Int) : super(context, attributeSet, defStyle) {
        initializeViews()
    }

    lateinit var button: Button
        private set

    private fun initializeViews() {
        val inflater = context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        inflater.inflate(R.layout.switch_state_view, this)

        button = bttnSwitchState
    }
}