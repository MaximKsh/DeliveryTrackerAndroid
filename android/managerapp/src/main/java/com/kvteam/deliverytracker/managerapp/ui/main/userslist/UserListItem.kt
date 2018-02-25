package com.kvteam.deliverytracker.managerapp.ui.main.userslist

import android.graphics.Color
import android.view.View
import com.kvteam.deliverytracker.managerapp.R
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.AbstractSectionableItem
import eu.davidea.flexibleadapter.items.IFlexible
import eu.davidea.viewholders.FlexibleViewHolder
import kotlinx.android.synthetic.main.fragment_user_list_item.view.*
import com.amulyakhare.textdrawable.TextDrawable
import eu.davidea.flexibleadapter.utils.DrawableUtils
import com.kvteam.deliverytracker.core.models.User


class UserListItem(
        val user: User,
        header: SubgroupListHeader)
    : AbstractSectionableItem<UserListItem.UserListViewHolder, SubgroupListHeader>(header) {

    override fun equals(other: Any?): Boolean {
        if (other is UserListItem) {
            return user.code.equals(other.user.code)
        }
        return false
    }

    override fun hashCode(): Int {
        return user.code!!.hashCode()
    }

    override fun getLayoutRes(): Int {
        return R.layout.fragment_user_list_item
    }

    override fun createViewHolder(view: View, adapter: FlexibleAdapter<out IFlexible<*>>?) : UserListViewHolder {
        return UserListViewHolder(view, adapter);
    }

    override fun bindViewHolder(adapter: FlexibleAdapter<out IFlexible<*>>?, holder: UserListViewHolder, position: Int,
            payloads: List<*>) {
        val context = holder.itemView.context

        val drawable = DrawableUtils.getSelectableBackgroundCompat(
                Color.WHITE, Color.parseColor("#dddddd"),
                DrawableUtils.getColorControlHighlight(context))

        DrawableUtils.setBackgroundCompat(holder.itemView, drawable)

        val materialAvatarDefault = TextDrawable.builder()
                .buildRound(user.name!![0].toString() + user.surname!![0].toString(), Color.LTGRAY)

        holder.ivUserAvatar.setImageDrawable(materialAvatarDefault)
        holder.tvName.text = user.name
        holder.tvSurname.text = user.surname
    }

    open class UserListViewHolder(val view: View, val adapter: FlexibleAdapter<out IFlexible<*>>?) : FlexibleViewHolder(view, adapter) {
        val tvName = view.tvName!!
        val tvSurname = view.tvSurname!!
        val ivUserAvatar = view.ivUserAvatar!!
        val ivAdminStatusIcon = view.ivAdminStatusIcon!!
    }
}