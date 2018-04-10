package com.kvteam.deliverytracker.core.ui

import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean

class SoftKeyboard(
        private val layout: ViewGroup,
        private val im: InputMethodManager) : View.OnFocusChangeListener {
    private var layoutBottom: Int = 0
    private val coords: IntArray
    private var isKeyboardShow: Boolean = false
    private val softKeyboardThread: SoftKeyboardChangesThread
    private var editTextList: MutableList<EditText>? = null

    private var tempView: View? = null // reference to a focused EditText

    private val layoutCoordinates: Int
        get() {
            layout.getLocationOnScreen(coords)
            return coords[1] + layout.height
        }

    // This handler will clear focus of selected EditText
    private val mHandler = object : Handler() {
        override fun handleMessage(m: Message) {
            when (m.what) {
                CLEAR_FOCUS -> if (tempView != null) {
                    tempView!!.clearFocus()
                    tempView = null
                }
            }
        }
    }

    init {
        keyboardHideByDefault()
        this.coords = IntArray(2)
        this.isKeyboardShow = false
        this.softKeyboardThread = SoftKeyboardChangesThread()
        this.softKeyboardThread.start()
    }


    fun openSoftKeyboard() {
        if (!isKeyboardShow) {
            layoutBottom = layoutCoordinates
            im.toggleSoftInput(0, InputMethodManager.SHOW_IMPLICIT)
            softKeyboardThread.keyboardOpened()
            isKeyboardShow = true
        }
    }

    fun closeSoftKeyboard() {
        if (isKeyboardShow) {
            im.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
            isKeyboardShow = false
        }
    }

    fun setSoftKeyboardCallback(mCallback: SoftKeyboardChanged) {
        softKeyboardThread.setCallback(mCallback)
    }

    fun unRegisterSoftKeyboardCallback() {
        softKeyboardThread.stopThread()
    }

    interface SoftKeyboardChanged {
        fun onSoftKeyboardHide()
        fun onSoftKeyboardShow()
    }

    private fun keyboardHideByDefault() {
        layout.isFocusable = true
        layout.isFocusableInTouchMode = true
    }

    /*
	 * InitEditTexts now handles EditTexts in nested views
	 * Thanks to Francesco Verheye (verheye.francesco@gmail.com)
	 */
    fun initEditTexts() {
        if (editTextList == null) {
            editTextList = ArrayList()
        } else {
            editTextList?.forEach { it.onFocusChangeListener = null }
            editTextList?.clear()
        }

        initEditTextsInternal(layout)

    }

    private fun initEditTextsInternal(view: ViewGroup) {
        val childCount = view.childCount
        for (i in 0 until childCount) {
            val v = view.getChildAt(i)

            if (v is ViewGroup) {
                initEditTextsInternal(v)
            }

            if (v is EditText) {
                v.onFocusChangeListener = this
                v.isCursorVisible = true
                editTextList!!.add(v)
            }
        }
    }

    /*
	 * OnFocusChange does update tempView correctly now when keyboard is still shown
	 * Thanks to Israel Dominguez (dominguez.israel@gmail.com)
	 */
    override fun onFocusChange(v: View, hasFocus: Boolean) {
        if (hasFocus) {
            tempView = v
            if (!isKeyboardShow) {
                layoutBottom = layoutCoordinates
                softKeyboardThread.keyboardOpened()
                isKeyboardShow = true
            }
        }
    }

    private inner class SoftKeyboardChangesThread : Thread() {
        private val started: AtomicBoolean
        private var mCallback: SoftKeyboardChanged? = null

        init {
            started = AtomicBoolean(true)
        }

        fun setCallback(mCallback: SoftKeyboardChanged) {
            this.mCallback = mCallback
        }

        override fun run() {
            while (started.get()) {
                // Wait until keyboard is requested to open
                synchronized(this) {
                    try {
                        (this as java.lang.Object).wait()
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    }

                }

                var currentBottomLocation = layoutCoordinates

                // There is some lag between open soft-keyboard function and when it really appears.
                while (currentBottomLocation == layoutBottom && started.get()) {
                    currentBottomLocation = layoutCoordinates
                }

                if (started.get()) {
                    Handler(Looper.getMainLooper()).post {
                        mCallback!!.onSoftKeyboardShow()
                    }
                }

                // When keyboard is opened from EditText, initial bottom location is greater than layoutBottom
                // and at some moment equals layoutBottom.
                // That broke the previous logic, so I added this new loop to handle this.
                while (currentBottomLocation >= layoutBottom && started.get()) {
                    currentBottomLocation = layoutCoordinates
                }

                // Now Keyboard is shown, keep checking layout dimensions until keyboard is gone
                while (currentBottomLocation != layoutBottom && started.get()) {
                    synchronized(this) {
                        try {
                            (this as java.lang.Object).wait(500)
                        } catch (e: InterruptedException) {
                            // TODO Auto-generated catch block
                            e.printStackTrace()
                        }

                    }
                    currentBottomLocation = layoutCoordinates
                }

                if (started.get()) {
                    Handler(Looper.getMainLooper()).post{
                        mCallback!!.onSoftKeyboardHide()
                    }
                }

                // if keyboard has been opened clicking and EditText.
                if (isKeyboardShow && started.get())
                    isKeyboardShow = false

                // if an EditText is focused, remove its focus (on UI thread)
                if (started.get())
                    mHandler.obtainMessage(CLEAR_FOCUS).sendToTarget()
            }
        }

        fun keyboardOpened() {
            synchronized(this) {
                (this as java.lang.Object).notify()
            }
        }

        fun stopThread() {
            synchronized(this) {
                started.set(false)
                (this as java.lang.Object).notify()
            }
        }

    }

    companion object {
        private val CLEAR_FOCUS = 0
    }
}
