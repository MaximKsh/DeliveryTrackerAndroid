package com.kvteam.deliverytracker.core.dataprovider

import com.kvteam.deliverytracker.core.dataprovider.base.BaseDataContainer
import com.kvteam.deliverytracker.core.models.Warehouse

class WarehouseDataContainer : BaseDataContainer<Warehouse>() {
    override val storageKey = "Warehouse"
}