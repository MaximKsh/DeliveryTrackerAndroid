package com.kvteam.deliverytracker.performerapp.ui.main.userslist

import com.kvteam.deliverytracker.core.models.User
import com.kvteam.deliverytracker.core.ui.BaseListFlexibleAdapter
import com.kvteam.deliverytracker.core.ui.IBaseListItemActions


class UserListFlexibleAdapter(noHeaderItems: MutableList<UserListItem>,
                              userActions: IBaseListItemActions<UserListItem>)
    : BaseListFlexibleAdapter<User, UserListItem, UserListItem.UserListViewHolder>(noHeaderItems, userActions)
