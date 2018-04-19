package com.kvteam.deliverytracker.core.dataprovider

import com.kvteam.deliverytracker.core.dataprovider.base.BaseCollectionDataComponent
import com.kvteam.deliverytracker.core.models.ClientAddress

class ClientAddressDataComponent (
        clientAddressDataContainer: ClientAddressDataContainer
) : BaseCollectionDataComponent<ClientAddress>(clientAddressDataContainer) {
    override fun entryFactory(): ClientAddress {
        return ClientAddress()
    }

}