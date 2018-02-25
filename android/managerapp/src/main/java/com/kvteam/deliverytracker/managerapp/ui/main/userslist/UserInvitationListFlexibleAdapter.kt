package com.kvteam.deliverytracker.managerapp.ui.main.userslist

import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import eu.davidea.flexibleadapter.FlexibleAdapter
import com.chauthai.swipereveallayout.ViewBinderHelper
import kotlinx.android.synthetic.main.fragment_invitation_list_item.view.*


class UserInvitationListFlexibleAdapter(private val list: MutableList<UserInvitationListItem>) : FlexibleAdapter<UserInvitationListItem>(list) {
    private val viewBinderHelper = ViewBinderHelper()

    init {
        viewBinderHelper.setOpenOnlyOne(true);
    }

    inner class CustomHolder(itemView: View, adapter: FlexibleAdapter<UserInvitationListItem>): UserInvitationListItem.UserInvitationListViewHolder(itemView, adapter) {
        val swipeRevealLayout = itemView.swipeRevealLayout!!
        val tvCancelInvitation = view.tvCancelInvitation!!
        val rlInvitationItem = view.rlInvitationItem!!
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int, payloads: MutableList<Any?>?) {
        var viewHolder = holder

        if (holder is UserInvitationListItem.UserInvitationListViewHolder) {
            viewHolder = CustomHolder(holder.itemView, this)

            viewHolder.tvCancelInvitation.setOnClickListener { _ ->
                list.removeAt(position)
                updateDataSet(list, true)
            }

            viewHolder.rlInvitationItem.setOnClickListener { _ ->
                Log.i("USER ITEM", "HELLO")
            }

            viewBinderHelper.bind(viewHolder.swipeRevealLayout, position.toString())
        }

        super.onBindViewHolder(viewHolder, position, payloads)
    }
}
