package com.kvteam.deliverytracker.managerapp.ui.main.userslist

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.IFlexible
import com.chauthai.swipereveallayout.ViewBinderHelper
import android.widget.TextView
import com.kvteam.deliverytracker.managerapp.R.id.textView
import com.chauthai.swipereveallayout.SwipeRevealLayout
import kotlinx.android.synthetic.main.fragment_manager_list_item.view.*


class UserListAdapter(private val list: List<UserListItem>) : FlexibleAdapter<UserListItem>(list) {
    private val viewBinderHelper = ViewBinderHelper()

    init {
        viewBinderHelper.setOpenOnlyOne(true);
    }

    class CustomHolder(itemView: View, adapter: FlexibleAdapter<UserListItem>): UserListItem.UserListViewHolder(itemView, adapter) {
        val swipeRevealLayout = itemView.swipeRevealLayout!!
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int, payloads: MutableList<Any?>?) {
        super.onBindViewHolder(holder, position, payloads)

        if (holder is UserListItem.UserListViewHolder) {
            val viewHolder = holder as CustomHolder
            viewBinderHelper.bind(viewHolder.swipeRevealLayout, list[position].userListModel.user.code)
        }
    }
}
