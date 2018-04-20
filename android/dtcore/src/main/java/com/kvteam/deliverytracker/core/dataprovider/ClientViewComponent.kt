package com.kvteam.deliverytracker.core.dataprovider

import com.kvteam.deliverytracker.core.dataprovider.base.BaseReferenceViewComponent
import com.kvteam.deliverytracker.core.models.Client
import com.kvteam.deliverytracker.core.models.ClientAddress
import com.kvteam.deliverytracker.core.models.ResponseReferencePackage
import com.kvteam.deliverytracker.core.webservice.IViewWebservice


class ClientViewComponent (
        viewWebservice: IViewWebservice,
        clientDataContainer: ClientDataContainer,
        private val clientAddressDataContainer: ClientAddressDataContainer
) : BaseReferenceViewComponent<Client>(viewWebservice, clientDataContainer) {
    override fun entryFactory(): Client {
        return Client()
    }

    override fun extractCollections(pack: ResponseReferencePackage, entry: Client) {
        for(collectionEntry in pack.collections) {
            val address = ClientAddress()
            address.fromMap(collectionEntry)
            clientAddressDataContainer.putEntry(address)
        }
    }
}