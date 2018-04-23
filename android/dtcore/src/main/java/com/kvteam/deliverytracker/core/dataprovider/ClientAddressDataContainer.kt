package com.kvteam.deliverytracker.core.dataprovider

import com.kvteam.deliverytracker.core.dataprovider.base.BaseCollectionDataContainer
import com.kvteam.deliverytracker.core.models.ClientAddress

class ClientAddressDataContainer : BaseCollectionDataContainer<ClientAddress>() {
    override val storageKey: String
        get() = "ClientAddressKey"

}