package com.kvteam.deliverytracker.performerapp.ui.main.userslist

import android.graphics.Color
import android.view.View
import com.amulyakhare.textdrawable.TextDrawable
import com.kvteam.deliverytracker.core.models.User
import com.kvteam.deliverytracker.core.roles.Role
import com.kvteam.deliverytracker.core.ui.BaseListHeader
import com.kvteam.deliverytracker.core.ui.BaseListItem
import com.kvteam.deliverytracker.performerapp.R
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.IFlexible
import kotlinx.android.synthetic.main.fragment_user_list_item.view.*


class UserListItem(val user: User, header: BaseListHeader)
        : BaseListItem<User, UserListItem.UserListViewHolder>(user, header) {
    override val key: String
        get() = user.code!!

    override fun createViewHolder(view: View, adapter: FlexibleAdapter<out IFlexible<*>>?) : UserListViewHolder {
        return UserListViewHolder(view, adapter)
    }

    override fun bindViewHolder(adapter: FlexibleAdapter<out IFlexible<*>>?, holder: UserListViewHolder, position: Int, payloads: MutableList<Any>?) {
        val text = StringBuilder(4)
        if(user.name?.isNotBlank() == true) {
            text.append(user.name!![0].toString())
        }
        if(user.surname?.isNotBlank() == true) {
            text.append(user.surname!![0].toString())
        }

        val materialAvatarDefault = TextDrawable.builder()
                .buildRound(text.toString(), Color.LTGRAY)

        holder.ivUserAvatar.setImageDrawable(materialAvatarDefault)
        holder.tvName.text = user.name
        holder.tvSurname.text = user.surname
        holder.ivOnlineStatus.visibility = if (user.online) View.VISIBLE else View.INVISIBLE
        holder.ivAdminStatusIcon.visibility = if (user.role == Role.Creator.id) View.VISIBLE else View.INVISIBLE
    }

    open class UserListViewHolder(view: View, adapter: FlexibleAdapter<out IFlexible<*>>?) : BaseListItem.BaseListViewHolder(view, adapter) {
        override val layoutID: Int
            get() = R.layout.fragment_user_list_item

        val tvName = view.tvName!!
        val tvSurname = view.tvSurname!!
        val ivUserAvatar = view.ivUserAvatar!!
        val ivOnlineStatus = view.ivOnlineStatus!!
        val ivAdminStatusIcon = view.ivAdminStatusIcon!!
    }
}
