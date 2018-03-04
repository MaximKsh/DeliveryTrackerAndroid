package com.kvteam.deliverytracker.managerapp.ui.main.userslist

import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import eu.davidea.flexibleadapter.FlexibleAdapter
import com.chauthai.swipereveallayout.ViewBinderHelper
import eu.davidea.flexibleadapter.items.IFlexible
import kotlinx.android.synthetic.main.fragment_invitation_list_item.view.*


class UserInvitationListFlexibleAdapter(
        private var noHeaderItems: MutableList<UserInvitationListItem>,
        private val invitationActions: IInvitationActions) : FlexibleAdapter<UserInvitationListItem>(noHeaderItems) {
    private val viewBinderHelper = ViewBinderHelper()

    init {
        viewBinderHelper.setOpenOnlyOne(true);
    }

    inner class CustomHolder(itemView: View, adapter: FlexibleAdapter<UserInvitationListItem>): UserInvitationListItem.UserInvitationListViewHolder(itemView, adapter) {
        val swipeRevealLayout = itemView.swipeRevealLayout!!
        val tvCancelInvitation = view.tvCancelInvitation!!
        val rlInvitationItem = view.rlInvitationItem!!
    }

    override fun onPostUpdate() {
        super.onPostUpdate()
        noHeaderItems = currentItems.filter { item: IFlexible<*> -> item is UserInvitationListItem }.toMutableList()
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int, payloads: MutableList<Any?>?) {
        var viewHolder = holder

        if (holder is UserInvitationListItem.UserInvitationListViewHolder) {
            viewHolder = CustomHolder(holder.itemView, this)

            val item = getItem(position)!!

            viewHolder.tvCancelInvitation.setOnClickListener { _ ->
                invitationActions.onDelete(this, noHeaderItems, item)
            }

            viewHolder.rlInvitationItem.setOnClickListener { _ ->
                invitationActions.onInvitationItemClicked(this, noHeaderItems, item)
            }

            viewBinderHelper.bind(viewHolder.swipeRevealLayout, item.invitation.invitationCode)
        }

        super.onBindViewHolder(viewHolder, position, payloads)
    }
}
