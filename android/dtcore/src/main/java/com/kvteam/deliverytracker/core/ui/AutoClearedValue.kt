package com.kvteam.deliverytracker.core.ui

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v7.app.AppCompatActivity
import android.arch.lifecycle.OnLifecycleEvent
import android.databinding.ViewDataBinding


class AutoClearedValue<T> {
    constructor(fragment: Fragment, mValue: T?, clearFunc: ((value: T?) -> Unit)? = null) {
        value = mValue
        val fragmentManager = fragment.fragmentManager
        fragmentManager.registerFragmentLifecycleCallbacks(
                object : FragmentManager.FragmentLifecycleCallbacks() {
                    override fun onFragmentViewDestroyed(fm: FragmentManager?, f: Fragment?) {
                        if (f === fragment) {
                            clearFunc?.invoke(this@AutoClearedValue.value)
                            this@AutoClearedValue.value = null
                            fragmentManager.unregisterFragmentLifecycleCallbacks(this)
                        }
                    }
                }, false)
    }

    constructor(activity: AppCompatActivity, mValue: T?, clearFunc: ((value: T?) -> Unit)? = null){
        value = mValue
        activity.lifecycle.addObserver(object: LifecycleObserver {
            @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
            fun onDestroy() {
                val value = this@AutoClearedValue.value
                clearFunc?.invoke(value)
                this@AutoClearedValue.value = null
                activity.lifecycle.removeObserver(this)
            }
        })
    }

    var value: T?
        private set

}
