package com.kvteam.deliverytracker.managerapp.ui.main.userslist

import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.text.Layout
import android.view.View
import com.kvteam.deliverytracker.managerapp.R
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import eu.davidea.flexibleadapter.items.AbstractHeaderItem
import eu.davidea.flexibleadapter.items.AbstractSectionableItem
import eu.davidea.flexibleadapter.items.IFlexible
import eu.davidea.viewholders.FlexibleViewHolder
import kotlinx.android.synthetic.main.fragment_manager_list_item.view.*
import kotlinx.android.synthetic.main.user_list_sticky_header_item.view.*
import com.amulyakhare.textdrawable.TextDrawable



class UserListItem(
        private val userListModel: UserListModel,
        header: UserListAlphabeticHeader)
    : AbstractSectionableItem<UserListItem.UserListViewHolder, UserListAlphabeticHeader>(header) {
    override fun equals(other: Any?): Boolean {
        if (other is UserListItem) {
            return this.userListModel.user.code.equals(other.userListModel.user.code)
        }
        return false
    }

    override fun hashCode(): Int {
        return userListModel.user.code!!.hashCode()
    }

    override fun getLayoutRes(): Int {
        return R.layout.fragment_manager_list_item
    }

    override fun createViewHolder(view: View, adapter: FlexibleAdapter<out IFlexible<*>>?) : UserListViewHolder {
        return UserListViewHolder(view, adapter);
    }

    override fun bindViewHolder(adapter: FlexibleAdapter<out IFlexible<*>>?, holder: UserListViewHolder, position: Int,
            payloads: List<*>) {
        val drawable = TextDrawable.builder()
                .buildRound(userListModel.user.name!![0].toString() + userListModel.user.surname!![0].toString(), Color.LTGRAY)

        holder.ivUserAvatar.setImageDrawable(drawable)
        holder.cbSelectUser.visibility = View.GONE
        holder.tvName.text = userListModel.user.name
        holder.tvSurname.text = userListModel.user.surname
        holder.cbSelectUser.isChecked = userListModel.isSelected
    }

    class UserListViewHolder(val view: View, val adapter: FlexibleAdapter<out IFlexible<*>>?) : FlexibleViewHolder(view, adapter) {
        val tvName = view.tvName!!
        val tvSurname = view.tvSurname!!
        val cbSelectUser = view.cbSelectUser!!
        val ivUserAvatar = view.ivUserAvatar!!
        val ivAdminStatusIcon = view.ivAdminStatusIcon!!
    }
}