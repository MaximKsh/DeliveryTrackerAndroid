package com.kvteam.deliverytracker.managerapp.ui.dropdowntop

import android.animation.ValueAnimator
import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.drawable.BitmapDrawable
import android.support.v4.app.FragmentActivity
import android.support.v4.content.ContextCompat
import com.kvteam.deliverytracker.managerapp.R
import kotlinx.android.synthetic.main.dropdown_top.*
import kotlinx.android.synthetic.main.toolbar.*
import java.util.concurrent.atomic.AtomicInteger
import kotlin.collections.ArrayList

class DropdownTop (val items: ArrayList<DropdownTopItemInfo>, val activity: FragmentActivity) {
    var lastSelectedIndex = AtomicInteger(0)
    val paddingSize = 10
    val itemHeight = 45
    val density = activity.resources.displayMetrics.density
    val height = (density * items.size * itemHeight + density * paddingSize).toInt()
    val toggleIcon = ContextCompat.getDrawable(activity, R.drawable.ic_expand_more_black_24dp)
    val toggleIconResized = BitmapDrawable(
            activity.resources,
            Bitmap.createScaledBitmap(
                    (toggleIcon as BitmapDrawable).bitmap,
                    Math.round(30 * density),
                    Math.round(30 * density),
                    true)
    )
    val rotatedToggleIcon = BitmapDrawable(activity.resources, rotateBitmap(toggleIconResized.bitmap, 180f))

    val dropdownTopItems: List<DropdownTopItem> = items.mapIndexed { index, dropdownItem -> DropdownTopItem(
            index,
            R.drawable.ic_supervisor_account_black_24dp,
            dropdownItem.name,
            dropdownItem.quantity,
            ::onItemSelected,
            index == 0,
            activity) }

    var isCollapsed = false

    fun update() {
        activity.toolbar_title.text = items[lastSelectedIndex.get()].name
        if (isCollapsed) {
            closeDropdown()
        }
        isCollapsed = false
        dropdownTopItems[lastSelectedIndex.get()].onLoadCompleted()
        dropdownTopItems.forEach { item -> if (item.index != lastSelectedIndex.get()) item.reset() }
    }

    private fun onItemSelected (index: Int) {
        if (index != lastSelectedIndex.get()) {
            dropdownTopItems[lastSelectedIndex.get()].reset()
        }
        lastSelectedIndex.set(index)
        items[index].onSelected(index)
    }

    private fun rotateBitmap (source: Bitmap, angle: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(angle)
        return Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, true)
    }

    private fun openDropdown () {
        val anim = ValueAnimator.ofInt(0, height)
        anim.addUpdateListener { valueAnimator ->
            val value = valueAnimator.animatedValue as Int
            val layoutParams = activity.ll_dropdown_top.layoutParams
            layoutParams.height = value
            activity.ll_dropdown_top.layoutParams = layoutParams
        }
        anim.duration = 100L * items.size
        anim.start()

        val anim2 = ValueAnimator.ofFloat(0f, 0.4f)
        anim2.addUpdateListener { valueAnimator ->
            val value = valueAnimator.animatedValue as Float
            activity.vBlackView.alpha = value
        }

        anim2.duration = 100L * items.size
        anim2.start()

        activity.toolbar_title.setCompoundDrawablesWithIntrinsicBounds(null, null, rotatedToggleIcon, null)
    }

    private fun closeDropdown () {
        val anim = ValueAnimator.ofInt(height, 0)
        anim.addUpdateListener { valueAnimator ->
            val value = valueAnimator.animatedValue as Int
            val layoutParams = activity.ll_dropdown_top.layoutParams
            layoutParams.height = value
            activity.ll_dropdown_top.layoutParams = layoutParams
        }
        anim.duration = 100L * items.size
        anim.start()

        val anim2 = ValueAnimator.ofFloat(0.4f, 0f)
        anim2.addUpdateListener { valueAnimator ->
            val value = valueAnimator.animatedValue as Float
            activity.vBlackView.alpha = value
        }

        anim2.duration = 100L * items.size
        anim2.start()

        activity.toolbar_title.setCompoundDrawablesWithIntrinsicBounds(null, null, toggleIconResized, null)
    }

    init {
        activity.toolbar_title.setCompoundDrawablesWithIntrinsicBounds(null, null, toggleIconResized, null)

        activity.toolbar_title.setOnClickListener { _ ->
            if (isCollapsed) {
                closeDropdown()
                isCollapsed = false
            } else {
                openDropdown()
                isCollapsed = true
            }
        }
    }
}
