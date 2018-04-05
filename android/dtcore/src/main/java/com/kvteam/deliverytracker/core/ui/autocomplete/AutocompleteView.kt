package com.kvteam.deliverytracker.core.ui.autocomplete

import android.content.Context
import android.graphics.PorterDuff
import android.util.AttributeSet
import android.widget.RelativeLayout
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.ProgressBar
import com.kvteam.deliverytracker.core.R
import kotlinx.android.synthetic.main.autocomplete_view.view.*


class AutocompleteView : RelativeLayout {

    constructor(context: Context) : super(context){
        initializeViews()
    }

    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet) {
        initializeViews()
    }

    constructor(context: Context, attributeSet: AttributeSet, defStyle: Int) : super(context, attributeSet, defStyle) {
        initializeViews()
    }

    lateinit var autoCompleteTextView: DelayAutoCompleteTextView
        private set

    private lateinit var progressBar: ProgressBar

    private lateinit var listSelectionButton: ImageView

    override fun onFinishInflate() {
        super.onFinishInflate()

        autoCompleteTextView = this.atvAutocomplete
        progressBar = this.pbProgress
        listSelectionButton = this.ivListSelection

        autoCompleteTextView.setLoadingIndicator(progressBar)

        val color = if(android.os.Build.VERSION.SDK_INT >= 23) {
            resources.getColor(R.color.colorGray, null)
        } else {
            @Suppress("DEPRECATION")
            resources.getColor(R.color.colorGray)
        }
        progressBar.indeterminateDrawable.setColorFilter(color, PorterDuff.Mode.SRC_IN)

    }

    private fun initializeViews() {
        val inflater = context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        inflater.inflate(R.layout.autocomplete_view, this)
    }

}