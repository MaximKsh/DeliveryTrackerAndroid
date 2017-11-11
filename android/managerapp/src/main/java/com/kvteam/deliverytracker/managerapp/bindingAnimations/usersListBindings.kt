package com.kvteam.deliverytracker.managerapp.bindingAnimations

import android.databinding.BindingAdapter
import android.support.v4.view.animation.FastOutSlowInInterpolator
import android.view.View
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import android.view.animation.TranslateAnimation

object ViewBusyBindings {
    private val INTERPOLATOR = FastOutSlowInInterpolator()

    private val animation: Animation
        get() {
            val anim = RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
            anim.interpolator = INTERPOLATOR
            anim.duration = 1400
            anim.repeatCount = TranslateAnimation.INFINITE
            anim.repeatMode = TranslateAnimation.RESTART
            return anim

        }

    @BindingAdapter("isBusy")
    fun setIsBusy(view: View, isBusy: Boolean) {
        val animation = view.animation
        if (isBusy && animation == null) {
            view.startAnimation(animation)
        } else if (animation != null) {
            animation.cancel()
            view.animation = null
        }
    }
}