package com.kvteam.deliverytracker.managerapp.ui.main.userslist

import android.graphics.Color
import android.view.View
import com.kvteam.deliverytracker.managerapp.R
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.AbstractSectionableItem
import eu.davidea.flexibleadapter.items.IFlexible
import eu.davidea.viewholders.FlexibleViewHolder
import com.amulyakhare.textdrawable.TextDrawable
import com.kvteam.deliverytracker.core.models.Invitation
import eu.davidea.flexibleadapter.utils.DrawableUtils
import kotlinx.android.synthetic.main.fragment_invitation_list_item.view.*


class UserInvitationListItem(
        val invitation: Invitation,
        header: SubgroupListHeader)
    : AbstractSectionableItem<UserInvitationListItem.UserInvitationListViewHolder, SubgroupListHeader>(header) {

    override fun equals(other: Any?): Boolean {
        if (other is UserInvitationListItem) {
            return invitation.invitationCode.equals(other.invitation.invitationCode)
        }
        return false
    }

    override fun hashCode(): Int {
        return invitation.invitationCode!!.hashCode()
    }

    override fun getLayoutRes(): Int {
        return R.layout.fragment_invitation_list_item
    }

    override fun createViewHolder(view: View, adapter: FlexibleAdapter<out IFlexible<*>>?) : UserInvitationListViewHolder {
        return UserInvitationListViewHolder(view, adapter);
    }

    override fun bindViewHolder(adapter: FlexibleAdapter<out IFlexible<*>>?, holder: UserInvitationListViewHolder, position: Int,
                                payloads: List<*>) {
        val context = holder.itemView.context

        val drawable = DrawableUtils.getSelectableBackgroundCompat(
                Color.WHITE, Color.parseColor("#dddddd"),
                DrawableUtils.getColorControlHighlight(context))

        DrawableUtils.setBackgroundCompat(holder.itemView, drawable)

        holder.tvName.text = invitation.preliminaryUser?.name
        holder.tvSurname.text = invitation.preliminaryUser?.surname
        holder.tvInvitationCode.text = invitation.invitationCode
        holder.tvExpiresAt.text = invitation.expires.toString()
    }

    open class UserInvitationListViewHolder(val view: View, val adapter: FlexibleAdapter<out IFlexible<*>>?) : FlexibleViewHolder(view, adapter) {
        val tvName = view.tvName!!
        val tvSurname = view.tvSurname!!
        val tvInvitationCode = view.tvInvitationCode!!
        val tvExpiresAt = view.tvExpiresAt!!
    }
}