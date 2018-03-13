package com.kvteam.deliverytracker.core.ui.dropdowntop

import android.animation.ValueAnimator
import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.drawable.BitmapDrawable
import android.support.v4.app.FragmentActivity
import android.support.v4.content.ContextCompat
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import com.kvteam.deliverytracker.core.R
import kotlinx.android.synthetic.main.dropdown_top.*
import kotlinx.android.synthetic.main.toolbar.*
import java.util.concurrent.atomic.AtomicInteger

class DropdownTop (var items: ArrayList<DropdownTopItemInfo>, val activity: FragmentActivity) {
    var lastSelectedIndex = AtomicInteger(0)
    val paddingSize = 10
    val itemHeight = 45
    val density = activity.resources.displayMetrics.density
    var height = 0
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
    lateinit var dropdownTopItems: List<DropdownTopItem>
    var isCollapsed: Boolean = false

    fun update() {
        activity.toolbar_title.text = items[lastSelectedIndex.get()].caption
        if (isCollapsed) {
            closeDropdown()
        }
        isCollapsed = false
        dropdownTopItems[lastSelectedIndex.get()].onLoadCompleted()
        dropdownTopItems.forEach { item -> if (item.index != lastSelectedIndex.get()) item.reset() }
    }

    private fun onItemSelected (index: Int) {
        val lsi = lastSelectedIndex.get()
        if (index != lsi) {
            dropdownTopItems[lastSelectedIndex.get()].reset()
        }
        lastSelectedIndex.set(index)
        items[index].onSelected(index)
    }

    fun updateDataSet  (items: ArrayList<DropdownTopItemInfo>) {
        activity.ll_dropdown_top.removeAllViews()

        height = (density * items.size * itemHeight + density * paddingSize).toInt()
        this.items = items
        lastSelectedIndex = AtomicInteger(0)
        dropdownTopItems = items.mapIndexed { index, dropdownItem ->
            DropdownTopItem(
                    index,
                    R.drawable.ic_group_black_24dp,
                    dropdownItem.caption,
                    dropdownItem.quantity,
                    ::onItemSelected,
                    index == 0,
                    activity)
        }

    }

    // TODO: move to controller
    fun setToolbarTitle(text: String) {
        activity.toolbar_title.text = text
    }

    fun enableDropdown() {
        activity.toolbar_title.setCompoundDrawablesWithIntrinsicBounds(null, null, toggleIconResized, null)
        activity.toolbar_title.isClickable = true
    }

    fun disableDropDown() {
        activity.toolbar_title.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
        activity.toolbar_title.isClickable = false
    }

    fun enableSearchMode (callback: (String) -> Unit) {
        disableDropDown()
        activity.toolbar_title.visibility = View.GONE
        activity.rlToolbarSearch.visibility = View.VISIBLE
        activity.etToolbarSearch.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(p0: Editable?) {
                callback(p0.toString())
            }
        })
    }

    fun disableSearchMode () {
        enableDropdown()
        activity.toolbar_title.visibility = View.VISIBLE
        activity.rlToolbarSearch.visibility = View.GONE
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

        activity.vBlackView.isClickable = true
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

        activity.vBlackView.isClickable = false
        activity.toolbar_title.setCompoundDrawablesWithIntrinsicBounds(null, null, toggleIconResized, null)
    }

    fun init() {
        activity.vBlackView.isClickable = false

        updateDataSet(items)

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

        activity.vBlackView.setOnClickListener { _ ->
//            lastSelectedIndex.set(-1)
            closeDropdown()
            isCollapsed = false
        }
    }
}
