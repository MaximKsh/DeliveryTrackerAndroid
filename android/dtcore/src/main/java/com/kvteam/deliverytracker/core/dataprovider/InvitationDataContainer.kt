package com.kvteam.deliverytracker.core.dataprovider

import com.kvteam.deliverytracker.core.models.Invitation

class InvitationDataContainer : BaseDataContainer<Invitation>() {
    override val storageKey = "Invitation"
}