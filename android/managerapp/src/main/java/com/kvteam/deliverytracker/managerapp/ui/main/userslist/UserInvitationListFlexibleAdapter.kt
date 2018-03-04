package com.kvteam.deliverytracker.managerapp.ui.main.userslist

import com.kvteam.deliverytracker.core.models.Invitation
import com.kvteam.deliverytracker.core.ui.BaseListFlexibleAdapter
import com.kvteam.deliverytracker.core.ui.IBaseListItemActions


class UserInvitationListFlexibleAdapter(noHeaderItems: MutableList<UserInvitationListItem>,
                                        invitationActions: IBaseListItemActions<UserInvitationListItem>)
    : BaseListFlexibleAdapter<Invitation, UserInvitationListItem, UserInvitationListItem.InvitationListViewHolder>(noHeaderItems, invitationActions) {}

