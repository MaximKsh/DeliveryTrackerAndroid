package com.kvteam.deliverytracker.managerapp.ui.dropdowntop

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.support.v4.app.FragmentActivity
import android.view.View
import com.kvteam.deliverytracker.managerapp.R
import com.kvteam.deliverytracker.managerapp.R.id.ll_dropdown_top
import kotlinx.android.synthetic.main.dropdown_top.*
import kotlinx.android.synthetic.main.dropdown_top_item.*
import kotlinx.android.synthetic.main.dropdown_top_item.view.*
import kotlinx.android.synthetic.main.toolbar.*
import java.util.*

class DropdownTop (val items: ArrayList<String>, val activity: FragmentActivity) {
    val toggleIcon = activity.resources.getDrawable(R.drawable.show_more_rotate)
    val paddingSize = 10
    val itemHeight = 45
    val density = activity.resources.displayMetrics.density
    val height = (density * items.size * itemHeight + density * paddingSize).toInt()

    var isCollapsed = false

    private fun onItemClick (view: View) {
        val rnd = Random()
        val color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256))
        view.setBackgroundColor(color)
    }

    init {
        items.forEach { name ->
            val item = activity.layoutInflater.inflate(R.layout.dropdown_top_item, activity.ll_dropdown_top,false);
            item.tvCategoryName.text = name
            item.ivCategoryIcon.setImageResource(R.drawable.ic_supervisor_account_black_24dp)
            item.tvCategoryQuantity.text = activity.resources.getString(R.string.ManagerApp_Dropdown_Top_Quantity, name.length);
            item.setOnClickListener{ view -> onItemClick(view) }
            activity.ll_dropdown_top.addView(item)
        }

        activity.toolbar_title.setOnClickListener { _ ->
            if (isCollapsed) {
                val anim = ValueAnimator.ofInt(height, 0)
                anim.addUpdateListener { valueAnimator ->
                    val value = valueAnimator.animatedValue as Int
                    val layoutParams = activity.ll_dropdown_top.layoutParams
                    layoutParams.height = value
                    activity.ll_dropdown_top.layoutParams = layoutParams
                }
                anim.duration = 100L * items.size
                anim.start()

                isCollapsed = false
            } else {
                val anim = ValueAnimator.ofInt(0, height)
                anim.addUpdateListener { valueAnimator ->
                    val value = valueAnimator.animatedValue as Int
                    val layoutParams = activity.ll_dropdown_top.layoutParams
                    layoutParams.height = value
                    activity.ll_dropdown_top.layoutParams = layoutParams
                }
                anim.duration = 100L * items.size
                anim.start()

                val anim2 = ObjectAnimator.ofInt(toggleIcon, "level", 0, 10000)
                anim2.start()
                isCollapsed = true
            }
        }
    }
}
