package com.kvteam.deliverytracker.managerapp.ui.main.userslist

interface IInvitationActions {
    fun onDelete(adapter: UserInvitationListFlexibleAdapter,
                 invitationList: MutableList<UserInvitationListItem>,
                 invitation: UserInvitationListItem)
    fun onInvitationItemClicked(adapter: UserInvitationListFlexibleAdapter,
                                invitationList: MutableList<UserInvitationListItem>,
                                invitation: UserInvitationListItem)
}