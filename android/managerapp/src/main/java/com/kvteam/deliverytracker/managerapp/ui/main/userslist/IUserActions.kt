package com.kvteam.deliverytracker.managerapp.ui.main.userslist

interface IUserActions {
   fun onDelete(adapter: UserListFlexibleAdapter,
                userList: MutableList<UserListItem>,
                user: UserListItem)
   fun onUserItemClicked(adapter: UserListFlexibleAdapter,
                         userList: MutableList<UserListItem>,
                         user: UserListItem)
}