package com.kvteam.deliverytracker.managerapp.ui.main.referenceslist.paymenttypes

import com.kvteam.deliverytracker.core.models.PaymentType
import com.kvteam.deliverytracker.core.ui.BaseListFlexibleAdapter
import com.kvteam.deliverytracker.core.ui.IBaseListItemActions

class PaymentTypesListFlexibleAdapter(referenceActions: IBaseListItemActions<PaymentTypeListItem>)
    : BaseListFlexibleAdapter<PaymentType, PaymentTypeListItem, PaymentTypeListItem.PaymentTypesListViewHolder>(referenceActions) {}
