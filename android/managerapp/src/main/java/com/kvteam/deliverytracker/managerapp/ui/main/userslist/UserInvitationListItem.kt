package com.kvteam.deliverytracker.managerapp.ui.main.userslist

import android.graphics.Color
import android.view.View
import com.kvteam.deliverytracker.core.common.ILocalizationManager
import com.kvteam.deliverytracker.core.models.Invitation
import com.kvteam.deliverytracker.core.roles.toRole
import com.kvteam.deliverytracker.core.ui.BaseListHeader
import com.kvteam.deliverytracker.core.ui.BaseListItem
import com.kvteam.deliverytracker.managerapp.R
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.IFlexible
import eu.davidea.flexibleadapter.utils.DrawableUtils
import kotlinx.android.synthetic.main.fragment_invitation_list_item.view.*


class UserInvitationListItem(
        val invitation: Invitation,
        header: BaseListHeader,
        private val lm: ILocalizationManager)
    : BaseListItem<Invitation, UserInvitationListItem.InvitationListViewHolder>(invitation, header) {

    override val key: String
        get() = invitation.invitationCode!!

    override fun createViewHolder(view: View, adapter: FlexibleAdapter<out IFlexible<*>>?) : InvitationListViewHolder {
        return InvitationListViewHolder(view, adapter);
    }

    override fun bindViewHolder(adapter: FlexibleAdapter<out IFlexible<*>>?, holder: InvitationListViewHolder, position: Int, payloads: MutableList<Any>?) {
        holder.tvName.text = invitation.preliminaryUser?.name
        holder.tvSurname.text = invitation.preliminaryUser?.surname
        holder.tvInvitationCode.text = invitation.invitationCode
        holder.tvExpiresAt.text = invitation.expires?.toString("dd.MM.yyyy")
        holder.tvPhoneNumber.text = invitation.preliminaryUser?.phoneNumber
        holder.tvRole.text = lm.getString(invitation.role?.toRole()?.localizationStringId ?: 0)
    }

    open class InvitationListViewHolder(view: View, adapter: FlexibleAdapter<out IFlexible<*>>?) : BaseListItem.BaseListViewHolder(view, adapter) {
        override val layoutID: Int
            get() = R.layout.fragment_invitation_list_item

        val tvName = view.tvName!!
        val tvSurname = view.tvSurname!!
        val tvInvitationCode = view.tvInvitationCode!!
        val tvExpiresAt = view.tvExpiresAt!!
        val tvPhoneNumber = view.tvPhoneNumber!!
        val tvRole = view.tvRole!!
    }
}
