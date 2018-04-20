package com.kvteam.deliverytracker.core.dataprovider

import com.kvteam.deliverytracker.core.dataprovider.base.BaseDataContainer
import com.kvteam.deliverytracker.core.models.Client
import java.util.*

class ClientDataContainer(
        private val clientAddressDataContainer: ClientAddressDataContainer
) : BaseDataContainer<Client>() {
    override val storageKey = "CLIENTS"

    override fun clearCollectionEntries(id: UUID?) {
        if (id != null) {
            clientAddressDataContainer.removeEntriesByParent(id)
            clientAddressDataContainer.removeDirtiesByParent(id)
        } else {
            clientAddressDataContainer.clearEntries()
            clientAddressDataContainer.clearDirties()
        }
    }
}
