package com.kvteam.deliverytracker.managerapp.ui.main.userslist

import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.view.ViewGroup
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.IFlexible
import com.chauthai.swipereveallayout.ViewBinderHelper
import android.widget.TextView
import com.kvteam.deliverytracker.managerapp.R.id.textView
import com.chauthai.swipereveallayout.SwipeRevealLayout
import eu.davidea.flexibleadapter.items.IHeader
import kotlinx.android.synthetic.main.fragment_manager_list_item.view.*


class UserListAdapter(private val list: MutableList<UserListItem>) : FlexibleAdapter<UserListItem>(list) {
    private val viewBinderHelper = ViewBinderHelper()

    init {
        viewBinderHelper.setOpenOnlyOne(true);
    }

    inner class CustomHolder(itemView: View, adapter: FlexibleAdapter<UserListItem>): UserListItem.UserListViewHolder(itemView, adapter) {
        val swipeRevealLayout = itemView.swipeRevealLayout!!
        val tvDeleteUser = view.tvDeleteUser!!
        val rlUserItem = view.rlUserItem!!
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int, payloads: MutableList<Any?>?) {
        var viewHolder = holder

        if (holder is UserListItem.UserListViewHolder) {
            viewHolder = CustomHolder(holder.itemView, this)
            val header = getGlobalPositionOf(viewHolder)

            viewHolder.tvDeleteUser.setOnClickListener { _ ->
                list.removeAt(position)
                updateDataSet(list, true)
            }

            viewHolder.rlUserItem.setOnClickListener { _ ->
                Log.i("USER ITEM", "HELLO")
            }

            viewBinderHelper.bind(viewHolder.swipeRevealLayout, position.toString())
        }

        super.onBindViewHolder(viewHolder, position, payloads)
    }
}
