package com.kvteam.deliverytracker.managerapp.ui.main.userslist

import com.kvteam.deliverytracker.core.models.User
import com.kvteam.deliverytracker.core.ui.BaseListFlexibleAdapter
import com.kvteam.deliverytracker.core.ui.IBaseListItemActions


class UserListFlexibleAdapter( userActions: IBaseListItemActions<UserListItem>)
    : BaseListFlexibleAdapter<User, UserListItem, UserListItem.UserListViewHolder>(userActions)
