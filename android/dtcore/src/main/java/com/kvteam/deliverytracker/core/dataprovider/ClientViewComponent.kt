package com.kvteam.deliverytracker.core.dataprovider

import com.kvteam.deliverytracker.core.models.Client
import com.kvteam.deliverytracker.core.webservice.IViewWebservice


class ClientViewComponent (
        viewWebservice: IViewWebservice,
        clientDataContainer: ClientDataContainer
) : BaseViewComponent<Client> (viewWebservice, clientDataContainer) {
    override fun entryFactory(): Client {
        return Client()
    }
}