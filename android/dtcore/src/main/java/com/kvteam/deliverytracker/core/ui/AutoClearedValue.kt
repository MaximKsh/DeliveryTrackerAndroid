package com.kvteam.deliverytracker.core.ui

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager

class AutoClearedValue<T>(fragment: Fragment, mValue: T?) {
    init {
        val fragmentManager = fragment.fragmentManager
        fragmentManager.registerFragmentLifecycleCallbacks(
                object : FragmentManager.FragmentLifecycleCallbacks() {
                    override fun onFragmentViewDestroyed(fm: FragmentManager?, f: Fragment?) {
                        if (f === fragment) {
                            this@AutoClearedValue.value = null
                            fragmentManager.unregisterFragmentLifecycleCallbacks(this)
                        }
                    }
                }, false)
    }

    var value: T? = mValue
        private set

}
