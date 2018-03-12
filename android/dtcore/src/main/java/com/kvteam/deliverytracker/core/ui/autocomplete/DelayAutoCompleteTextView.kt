package com.kvteam.deliverytracker.core.ui.autocomplete

import android.content.Context
import android.os.Handler
import android.os.Message
import android.util.AttributeSet
import android.view.View
import android.widget.ProgressBar
import android.widget.AutoCompleteTextView


class DelayAutoCompleteTextView(context: Context, attrs: AttributeSet) : AutoCompleteTextView(context, attrs) {

    private val messageTextChanged = 100
    private val defaultAutocompleteDelay = 750L

    private var mAutoCompleteDelay = defaultAutocompleteDelay
    private var mLoadingIndicator: ProgressBar? = null

    private val mHandler = object : Handler() {
        override fun handleMessage(msg: Message) {
            super@DelayAutoCompleteTextView.performFiltering(msg.obj as CharSequence, msg.arg1)
        }
    }

    fun setLoadingIndicator(progressBar: ProgressBar) {
        mLoadingIndicator = progressBar
    }

    fun setAutoCompleteDelay(autoCompleteDelay: Long) {
        mAutoCompleteDelay = autoCompleteDelay
    }

    override fun performFiltering(text: CharSequence, keyCode: Int) {
        if (mLoadingIndicator != null) {
            mLoadingIndicator!!.visibility = View.VISIBLE
        }
        mHandler.removeMessages(messageTextChanged)
        mHandler.sendMessageDelayed(mHandler.obtainMessage(messageTextChanged, text), mAutoCompleteDelay)
    }

    override fun onFilterComplete(count: Int) {
        if (mLoadingIndicator != null) {
            mLoadingIndicator!!.visibility = View.GONE
        }
        super.onFilterComplete(count)
    }
}