package com.kvteam.deliverytracker.core.ui.autocomplete

import android.content.Context
import android.graphics.Point
import android.graphics.Rect
import android.os.Handler
import android.os.Message
import android.util.AttributeSet
import android.view.View
import android.widget.ProgressBar
import android.widget.AutoCompleteTextView
import java.lang.ref.WeakReference
import android.os.Build
import android.annotation.TargetApi
import android.widget.AdapterView


open class DelayAutoCompleteTextView(context: Context, attrs: AttributeSet) : AutoCompleteTextView(context, attrs) {
    private class FilterMessageHandler(view : DelayAutoCompleteTextView): Handler() {
        private val viewReference = WeakReference<DelayAutoCompleteTextView>(view)
        override fun handleMessage(msg: Message?) {
            val view = viewReference.get()
            if(view != null && msg != null) {
                view.performFilteringSuper(msg.obj as CharSequence, msg.arg1)
            }
        }
    }

    private val messageTextChanged = 100
    private val defaultAutocompleteDelay = 750L

    private val handler = FilterMessageHandler(this)

    private var mAutoCompleteDelay = defaultAutocompleteDelay
    private var mLoadingIndicator: ProgressBar? = null

    override fun dismissDropDown() {}

    override fun performFiltering(text: CharSequence, keyCode: Int) {
        mLoadingIndicator?.visibility = View.VISIBLE
        handler.removeMessages(messageTextChanged)
        handler.sendMessageDelayed(handler.obtainMessage(messageTextChanged, text), mAutoCompleteDelay)
    }

    override fun onFilterComplete(count: Int) {
        mLoadingIndicator?.visibility = View.GONE
        super.onFilterComplete(count)
    }

    override fun replaceText(sequence: CharSequence) {
        // DO NOTHING BY DEFAULT
    }

    fun hideDropdown () {
        super.dismissDropDown()
    }

    fun setLoadingIndicator(progressBar: ProgressBar) {
        mLoadingIndicator = progressBar
    }

    fun setAutoCompleteDelay(autoCompleteDelay: Long) {
        mAutoCompleteDelay = autoCompleteDelay
    }

    private fun performFilteringSuper(text: CharSequence, keyCode: Int) {
        super.performFiltering(text, keyCode)
    }
}