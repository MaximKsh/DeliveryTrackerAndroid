package com.kvteam.deliverytracker.managerapp.ui.dropdowntop

import android.os.CountDownTimer
import android.support.v4.app.FragmentActivity
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import com.kvteam.deliverytracker.managerapp.R
import kotlinx.android.synthetic.main.dropdown_top.*
import kotlinx.android.synthetic.main.dropdown_top_item.view.*
import java.util.concurrent.atomic.AtomicBoolean

class DropdownTopItem (
        val index: Int,
        val icon: Int,
        val name: String,
        val quantity: Int,
        val onSelect: (Int) -> Unit,
        var isSelected: Boolean = false,
        val activity: FragmentActivity){

    val layout = activity.layoutInflater.inflate(R.layout.dropdown_top_item, activity.ll_dropdown_top,false)
    var isLoading = AtomicBoolean(false)

    private fun handleSelectionInner () {
        if (isLoading.get())
            return
        layout.aviLoadingIndicator.visibility = View.VISIBLE
        layout.ivSelectedIcon.visibility = View.GONE
        isLoading.set(true)
        onSelect(index)
    }

    fun onLoadCompleted () {
        layout.ivSelectedIcon.visibility = View.VISIBLE
        layout.aviLoadingIndicator.visibility = View.GONE
        isSelected = true
        isLoading.set(false)
    }

    init {
        layout.tvCategoryName.text = name
        layout.ivCategoryIcon.setImageResource(R.drawable.ic_supervisor_account_black_24dp)
        layout.tvCategoryQuantity.text = activity.resources.getString(R.string.ManagerApp_Dropdown_Top_Quantity, quantity)
        layout.ivSelectedIcon.visibility = if (isSelected) View.VISIBLE else View.GONE
        layout.setOnClickListener{ _ -> handleSelectionInner() }
        activity.ll_dropdown_top.addView(layout)
    }

    fun reset () {
        layout.ivSelectedIcon.visibility = View.GONE
        layout.aviLoadingIndicator.visibility = View.GONE
        isLoading.set(false)
        isSelected = false
    }
}
