package com.kvteam.deliverytracker.managerapp.ui.main.referenceslist.products

import com.kvteam.deliverytracker.core.models.Product
import com.kvteam.deliverytracker.core.ui.BaseListFlexibleAdapter
import com.kvteam.deliverytracker.core.ui.IBaseListItemActions

class ProductsListFlexibleAdapter(referenceActions: IBaseListItemActions<ProductListItem>)
    : BaseListFlexibleAdapter<Product, ProductListItem, ProductListItem.ProductsListViewHolder>(referenceActions) {
}
