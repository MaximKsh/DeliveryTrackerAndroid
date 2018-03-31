package com.kvteam.deliverytracker.core.ui

import android.content.Context
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import com.kvteam.deliverytracker.core.ui.toolbar.ToolbarController

abstract class DeliveryTrackerFragment: Fragment() {
    protected val dtActivity
       get() = activity as DeliveryTrackerActivity

    protected val toolbarController
        get() = dtActivity.toolbarController

    protected val dropdownTop
        get() = toolbarController.dropDownTop

    val name: String = this.javaClass.simpleName

    init {
        arguments = Bundle()
    }

    // TODO: is it working?
    override fun onCreateAnimation(transit: Int, enter: Boolean, nextAnim: Int): Animation? {
        var animation: Animation? = super.onCreateAnimation(transit, enter, nextAnim)

        if (animation == null && nextAnim != 0) {
            animation = AnimationUtils.loadAnimation(activity, nextAnim)
        }

        if (animation != null) {
            view!!.setLayerType(View.LAYER_TYPE_HARDWARE, null)

            animation.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationRepeat(p0: Animation?) {
                }

                override fun onAnimationStart(p0: Animation?) {
                }

                override fun onAnimationEnd(animation: Animation) {
                    view?.setLayerType(View.LAYER_TYPE_NONE, null)
                }
            })
        }

        return animation
    }

    protected open fun configureToolbar(toolbar: ToolbarController) {
        toolbar.disableDropDown()
        toolbar.disableSearchMode()
    }

    protected open fun configureFloatingActionButton(button: FloatingActionButton) {
        button.visibility = View.GONE
    }

    open fun refreshMenuItems() {

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configureToolbar(toolbarController)

        val fabButton = dtActivity.fabButton
        if(fabButton != null) {
            configureFloatingActionButton(dtActivity.fabButton!!)
        }
    }

    override fun onPause() {
        super.onPause()
        val view =  activity!!.currentFocus
        if (view != null) {
            val imm = activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

}