package com.kvteam.deliverytracker.managerapp.ui.main.referenceslist

import com.kvteam.deliverytracker.core.models.PaymentType
import com.kvteam.deliverytracker.core.models.User
import com.kvteam.deliverytracker.core.ui.BaseListFlexibleAdapter
import com.kvteam.deliverytracker.core.ui.IBaseListItemActions
import com.kvteam.deliverytracker.managerapp.ui.main.userslist.UserListItem

class ReferenceListFlexibleAdapter(noHeaderItems: MutableList<ReferenceListItem>,
                              referenceActions: IBaseListItemActions<ReferenceListItem>)
    : BaseListFlexibleAdapter<PaymentType, ReferenceListItem, ReferenceListItem.ReferenceListViewHolder>(noHeaderItems, referenceActions) {}
