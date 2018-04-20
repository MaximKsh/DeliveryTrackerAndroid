package com.kvteam.deliverytracker.core.dataprovider

import com.kvteam.deliverytracker.core.dataprovider.base.BaseViewComponent
import com.kvteam.deliverytracker.core.models.Invitation
import com.kvteam.deliverytracker.core.webservice.IViewWebservice

class InvitationViewComponent (
    viewWebservice: IViewWebservice,
    dataContainer: InvitationDataContainer
) : BaseViewComponent<Invitation>(viewWebservice, dataContainer) {
    override fun entryFactory(): Invitation {
        return Invitation()
    }

}