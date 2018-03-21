package com.kvteam.deliverytracker.core.dataprovider

import com.kvteam.deliverytracker.core.models.Client

class ClientDataContainer : BaseDataContainer<Client>() {
    override val storageKey = "CLIENTS"
}
