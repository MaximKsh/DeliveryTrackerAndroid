package com.kvteam.deliverytracker.core.dataprovider

import com.kvteam.deliverytracker.core.models.Product

class ProductDataContainer : BaseDataContainer<Product>() {
    override val storageKey = "Product"
}