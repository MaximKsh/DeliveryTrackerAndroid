package com.kvteam.deliverytracker.core.dataprovider

import com.kvteam.deliverytracker.core.models.Product
import com.kvteam.deliverytracker.core.webservice.IViewWebservice

class ProductViewComponent  (
        viewWebservice: IViewWebservice,
        dataContainer: ProductDataContainer
) : BaseViewComponent<Product>(viewWebservice, dataContainer) {
    override fun entryFactory(): Product {
        return Product()
    }

}