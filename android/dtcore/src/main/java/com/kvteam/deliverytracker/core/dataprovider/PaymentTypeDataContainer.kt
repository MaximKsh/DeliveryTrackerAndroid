package com.kvteam.deliverytracker.core.dataprovider

import com.kvteam.deliverytracker.core.models.PaymentType

class PaymentTypeDataContainer : BaseDataContainer<PaymentType>() {
    override val storageKey = "PaymentTypes"

}