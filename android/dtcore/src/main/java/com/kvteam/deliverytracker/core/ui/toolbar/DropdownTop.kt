package com.kvteam.deliverytracker.core.ui.toolbar

import android.animation.ValueAnimator
import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.drawable.BitmapDrawable
import android.support.v4.app.FragmentActivity
import android.support.v4.content.ContextCompat
import android.view.View
import com.kvteam.deliverytracker.core.R
import com.kvteam.deliverytracker.core.async.launchUI
import com.kvteam.deliverytracker.core.common.EMPTY_STRING
import kotlinx.android.synthetic.main.dropdown_top.*
import kotlinx.android.synthetic.main.toolbar.*
import java.util.concurrent.atomic.AtomicInteger

class DropdownTop (
        initialItems: ArrayList<DropdownTopItemInfo>,
        private val activity: FragmentActivity) {
    private val paddingSize = 10
    private val itemHeight = 45
    private val density = activity.resources.displayMetrics.density
    private var height = 0
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
    private lateinit var dropdownTopItems: List<DropdownTopItem>
    private var isCollapsed: Boolean = true
    private var initialized = false

    private val searchHeight
        get() = activity.rvDropdownSearch.height

    private val searchHeightEffective
        get() = if(activity.rvDropdownSearch.visibility == View.VISIBLE) searchHeight else 0

    val lastSelectedIndex = AtomicInteger(0)

    var items = initialItems
        private set

    fun init() {
        initialized = true
        updateDataSet(items)

        showIconCollapsed()

        activity.toolbar_title.setOnClickListener { _ ->
            if (isCollapsed) {
                openDropdown()
            } else {
                closeDropdown()
            }
        }
        activity.vBlackView.setOnClickListener { closeDropdown() }
    }

    fun update() {
        checkInitialized()
        activity.toolbar_title.text = items[lastSelectedIndex.get()].caption
        if (!isCollapsed) {
            closeDropdown()
        }
        dropdownTopItems[lastSelectedIndex.get()].onLoadCompleted()
        dropdownTopItems.forEach { item -> if (item.index != lastSelectedIndex.get()) item.reset() }
    }

    fun updateDataSet  (items: ArrayList<DropdownTopItemInfo>) {
        checkInitialized()
        activity.ll_dropdown_content.removeAllViews()
        height = (searchHeightEffective + density * items.size * itemHeight + density * paddingSize).toInt()
        this.items = items
        var idx = lastSelectedIndex.get()
        idx = if(0 <= idx && idx < items.size) idx else 0
        lastSelectedIndex.set(idx)
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

    var searchText: String
        get() {
            checkInitialized()
            return if (activity.rvDropdownSearch.visibility == View.VISIBLE) {
                activity.etDropdownSearch.text.toString()
            } else {
                return EMPTY_STRING
            }
        }
        set(value) {
            activity.etDropdownSearch.setText(value)
        }

    fun showSearch(searchAction: suspend (String) -> Unit = {},
                   refreshDigest:suspend () -> Unit = {}) {
        checkInitialized()
        activity.rvDropdownSearch.visibility = View.VISIBLE
        activity.ivDropdownSearch.setOnClickListener{
            launchUI {
                closeDropdown()
                searchAction(activity.etDropdownSearch.text.toString())
            }
        }
        activity.ivDropdownRefresh.setOnClickListener {
            launchUI {
                refreshDigest()
            }
        }
    }

    fun hideSearch() {
        checkInitialized()
        activity.rvDropdownSearch.visibility = View.GONE
        clearSearchText()
        activity.ivDropdownSearch.setOnClickListener { }
        activity.ivDropdownRefresh.setOnClickListener { }
    }

    fun clearSearchText() {
        checkInitialized()
        activity.etDropdownSearch.setText(EMPTY_STRING)
    }

    fun hideIcon() {
        checkInitialized()
        activity.toolbar_title.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
    }

    fun showIconCollapsed() {
        checkInitialized()
        activity.toolbar_title.setCompoundDrawablesWithIntrinsicBounds(null, null, toggleIconResized, null)
    }

    fun showIconOpen() {
        checkInitialized()
        activity.toolbar_title.setCompoundDrawablesWithIntrinsicBounds(null, null, rotatedToggleIcon, null)
    }

    private fun rotateBitmap (source: Bitmap, angle: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(angle)
        return Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, true)
    }

    private fun openDropdown () {
        if(!isCollapsed) {
            return
        }

        val anim = ValueAnimator.ofInt(0, height)
        anim.addUpdateListener { valueAnimator ->
            val value = valueAnimator.animatedValue as Int
            val layoutParams = activity.ll_dropdown_top.layoutParams
            layoutParams.height = value
            activity.ll_dropdown_top.layoutParams = layoutParams
        }
        anim.duration = 75L * items.size
        anim.start()

        val anim2 = ValueAnimator.ofFloat(0f, 0.4f)
        anim2.addUpdateListener { valueAnimator ->
            val value = valueAnimator.animatedValue as Float
            activity.vBlackView.alpha = value
            if(value == 0f) {
                activity.vBlackView.visibility = View.VISIBLE
            }
        }

        anim2.duration = 75L * items.size
        anim2.start()

        showIconOpen()
        isCollapsed = false
    }

    private fun closeDropdown () {
        if(isCollapsed) {
            return
        }

        val anim = ValueAnimator.ofInt(height, 0)
        anim.addUpdateListener { valueAnimator ->
            val value = valueAnimator.animatedValue as Int
            val layoutParams = activity.ll_dropdown_top.layoutParams
            layoutParams.height = value
            activity.ll_dropdown_top.layoutParams = layoutParams
        }
        anim.duration = 75L * items.size
        anim.start()

        val anim2 = ValueAnimator.ofFloat(0.4f, 0f)
        anim2.addUpdateListener { valueAnimator ->
            val value = valueAnimator.animatedValue as Float
            activity.vBlackView.alpha = value
            if(value == 0f) {
                activity.vBlackView.visibility = View.GONE
            }
        }

        anim2.duration = 75L * items.size
        anim2.start()

        showIconCollapsed()
        isCollapsed = true
    }

    private fun onItemSelected (index: Int) = launchUI {
        val lsi = lastSelectedIndex.get()
        if (index != lsi) {
            dropdownTopItems[lastSelectedIndex.get()].reset()
        }
        lastSelectedIndex.set(index)
        items[index].onSelected(index)
    }

    private fun checkInitialized() {
        if(!initialized) throw IllegalStateException("Dropdown wasn't initialize")
    }
}
