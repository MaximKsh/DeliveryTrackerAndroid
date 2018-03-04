package com.kvteam.deliverytracker.managerapp.ui.main.userslist

import android.support.v7.widget.RecyclerView
import android.view.View
import com.chauthai.swipereveallayout.ViewBinderHelper
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.IFlexible
import kotlinx.android.synthetic.main.fragment_user_list_item.view.*


class UserListFlexibleAdapter(
        private var noHeaderItems: MutableList<UserListItem>,
        private val userActions: IUserActions) : FlexibleAdapter<UserListItem>(noHeaderItems) {
    private val viewBinderHelper = ViewBinderHelper()


    init {
        viewBinderHelper.setOpenOnlyOne(true);
    }

    inner class CustomHolder(itemView: View, adapter: FlexibleAdapter<UserListItem>): UserListItem.UserListViewHolder(itemView, adapter) {
        val swipeRevealLayout = itemView.swipeRevealLayout!!
        val tvDeleteUser = view.tvDeleteUser!!
        val rlUserItem = view.rlUserItem!!
    }

    override fun onPostUpdate() {
        super.onPostUpdate()
        noHeaderItems = currentItems.filter { item: IFlexible<*> -> item is UserListItem }.toMutableList()
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int, payloads: MutableList<Any?>?) {
        var viewHolder = holder

        if (holder is UserListItem.UserListViewHolder) {
            viewHolder = CustomHolder(holder.itemView, this)

            val item = getItem(position)!!

            viewHolder.tvDeleteUser.setOnClickListener {
                userActions.onDelete(this, noHeaderItems, item)
            }

            viewHolder.rlUserItem.setOnClickListener {
                userActions.onUserItemClicked(this, noHeaderItems, item)
            }

            viewBinderHelper.bind(viewHolder.swipeRevealLayout, item.user.code)
        }

        super.onBindViewHolder(viewHolder, position, payloads)
    }
}
