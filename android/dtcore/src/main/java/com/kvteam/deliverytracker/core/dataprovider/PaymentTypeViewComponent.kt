package com.kvteam.deliverytracker.core.dataprovider

import com.kvteam.deliverytracker.core.models.PaymentType
import com.kvteam.deliverytracker.core.webservice.IViewWebservice
class PaymentTypeViewComponent(
        viewWebservice: IViewWebservice,
        dataContainer: PaymentTypeDataContainer
) : BaseViewComponent<PaymentType>(viewWebservice, dataContainer) {
    override fun entryFactory(): PaymentType = PaymentType()

}