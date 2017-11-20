package com.kvteam.deliverytracker.managerapp.ui.main.userslist

import com.kvteam.deliverytracker.core.models.UserModel


interface UserItemActions {
    fun onCallClick(user: UserModel)
    fun onChatClick(user: UserModel)
    fun onSelectClick(userListModel: UserListModel)
    fun onItemClick(user: UserModel)
}