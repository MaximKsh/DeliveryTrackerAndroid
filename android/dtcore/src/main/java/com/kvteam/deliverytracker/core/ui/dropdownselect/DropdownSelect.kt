package com.kvteam.deliverytracker.core.ui.dropdownselect

import android.animation.ValueAnimator
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.drawable.BitmapDrawable
import android.support.v4.app.FragmentActivity
import android.support.v4.content.ContextCompat
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import com.kvteam.deliverytracker.core.R
import kotlinx.android.synthetic.main.dropdown_select.view.*
import kotlinx.android.synthetic.main.dropdown_select_item.view.*

class DropdownSelect<out T>(
        val title: String,
        val data: List<DropdownSelectItem<T>>,
        val preselectedIndex: Int?,
        private val onSelectCallback: (Int, Int) -> Unit,
        private val displayTextSelector: (T) -> String,
        val container: ViewGroup,
        val activity: FragmentActivity) {

    private val density = activity.resources.displayMetrics.density
    private val itemHeight = 50
    private val height = (density * data.size * itemHeight).toInt()
    private val toggleIcon = ContextCompat.getDrawable(activity, R.drawable.ic_expand_more_black_24dp)
    private val toggleIconResized = BitmapDrawable(
            activity.resources,
            Bitmap.createScaledBitmap(
                    (toggleIcon as BitmapDrawable).bitmap,
                    Math.round(30 * density),
                    Math.round(30 * density),
                    true)
    )
    private val rotatedToggleIcon =
            BitmapDrawable(activity.resources, rotateBitmap(toggleIconResized.bitmap, 180f))

    private val dropdownSelectViewGroup = activity.layoutInflater.inflate(R.layout.dropdown_select, container, true)

    private var selectedIndex = preselectedIndex?: -1

    private var isCollapsed = true

    private val selectedItemsList = mutableListOf<DropdownSelectItem<T>>()

    private var drawnItemsList = if (preselectedIndex == null) data
                                 else  {
                                     selectedItemsList.add(data[preselectedIndex])
                                     selectedItemsList
                                 }

    init {
        dropdownSelectViewGroup.tvDropdownSelectTitle.text = title
        dropdownSelectViewGroup.tvDropdownSelectCollapseIcon.setImageDrawable(toggleIconResized)
        dropdownSelectViewGroup.rlDropdownSelectTitleContainer.setOnClickListener { _ ->
            when (isCollapsed) {
                true -> {
                    openDropDownSelect()
                    drawnItemsList = data
                    refreshDropDownSelect()
                }
                false -> {
                    drawnItemsList = selectedItemsList.toList()
                    refreshDropDownSelect()
                    closeDropdownSelect()
                }
            }
        }
        if (preselectedIndex != null) {
            val layoutParams = dropdownSelectViewGroup.llDropdownSelect.layoutParams
            layoutParams.height = (density * itemHeight).toInt()
            dropdownSelectViewGroup.llDropdownSelect.layoutParams = layoutParams
        }
        refreshDropDownSelect()
    }

    private fun openDropDownSelect() {
        isCollapsed = false
        dropdownSelectViewGroup.tvDropdownSelectCollapseIcon.setImageDrawable(rotatedToggleIcon)
        val anim = ValueAnimator.ofInt((density * selectedItemsList.size * itemHeight).toInt(), height)
        anim.addUpdateListener { valueAnimator ->
            val value = valueAnimator.animatedValue as Int
            val layoutParams = dropdownSelectViewGroup.llDropdownSelect.layoutParams
            layoutParams.height = value
            dropdownSelectViewGroup.llDropdownSelect.layoutParams = layoutParams
        }
        anim.duration = 75L * data.size
        anim.start()
    }

    private fun closeDropdownSelect() {
        isCollapsed = true
        dropdownSelectViewGroup.tvDropdownSelectCollapseIcon.setImageDrawable(toggleIcon)
        val anim = ValueAnimator.ofInt(height, (density * selectedItemsList.size * itemHeight).toInt())
        anim.addUpdateListener { valueAnimator ->
            val value = valueAnimator.animatedValue as Int
            val layoutParams = dropdownSelectViewGroup.llDropdownSelect.layoutParams
            layoutParams.height = value
            dropdownSelectViewGroup.llDropdownSelect.layoutParams = layoutParams
        }
        anim.duration = 75L * data.size
        anim.start()
    }


    private fun rotateBitmap (source: Bitmap, angle: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(angle)
        return Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, true)
    }

    private fun onSelect (index: Int) {
        if (!isCollapsed) {
            selectedIndex = index
            if (selectedItemsList.size > 0) {
                selectedItemsList[0] = data[index]
            } else {
                selectedItemsList.add(data[index])
            }
            drawnItemsList = selectedItemsList
            closeDropdownSelect()
        }
        onSelectCallback(selectedIndex, selectedIndex)
        refreshDropDownSelect()
    }

    fun refreshDropDownSelect () {
        drawnItemsList.forEachIndexed { index, dataItem ->
            val layout = if (dropdownSelectViewGroup.llDropdownSelect.childCount > index)
                dropdownSelectViewGroup.llDropdownSelect.getChildAt(index) else
                activity.layoutInflater.inflate(R.layout.dropdown_select_item, dropdownSelectViewGroup.llDropdownSelect, false)

            layout.ivSelectedIcon.visibility = View.GONE
            layout.tvName.text = displayTextSelector(dataItem.data)
            layout.setOnClickListener { onSelect(index) }
            if (dataItem.isLink) {
                layout.tvName.setTextColor(Color.GREEN)
            }
            if (index == selectedIndex && drawnItemsList != selectedItemsList) {
                layout.ivSelectedIcon.visibility = View.VISIBLE
            }

            if (dropdownSelectViewGroup.llDropdownSelect.childCount <= index) {
                dropdownSelectViewGroup.llDropdownSelect.addView(layout)
            }
            if (dataItem.isDisabled) {
                layout.isClickable = false
                layout.tvName.setTextColor(Color.LTGRAY)
            }
        }
    }
}