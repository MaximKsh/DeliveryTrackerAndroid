package com.kvteam.deliverytracker.core.dataprovider

import com.kvteam.deliverytracker.core.models.Warehouse
import com.kvteam.deliverytracker.core.webservice.IViewWebservice

class WarehouseViewComponent  (
        viewWebservice: IViewWebservice,
        dataContainer: WarehouseDataContainer
) : BaseViewComponent<Warehouse>(viewWebservice, dataContainer) {
    override fun entryFactory(): Warehouse {
        return Warehouse()
    }

}