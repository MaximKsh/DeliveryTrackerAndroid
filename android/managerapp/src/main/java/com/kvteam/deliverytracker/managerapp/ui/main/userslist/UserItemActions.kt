package com.kvteam.deliverytracker.managerapp.ui.main.userslist

import com.kvteam.deliverytracker.core.models.User


interface UserItemActions {
    fun onCallClick(user: User)
    fun onChatClick(user: User)
    fun onSelectClick(userListModel: UserListModel)
    fun onItemClick(user: User)
}