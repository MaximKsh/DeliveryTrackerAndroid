package com.kvteam.deliverytracker.core.dataprovider

import com.kvteam.deliverytracker.core.dataprovider.base.BaseReferenceViewComponent
import com.kvteam.deliverytracker.core.models.Warehouse
import com.kvteam.deliverytracker.core.webservice.IViewWebservice

class WarehouseViewComponent  (
        viewWebservice: IViewWebservice,
        dataContainer: WarehouseDataContainer
) : BaseReferenceViewComponent<Warehouse>(viewWebservice, dataContainer) {
    override fun entryFactory(): Warehouse {
        return Warehouse()
    }

}