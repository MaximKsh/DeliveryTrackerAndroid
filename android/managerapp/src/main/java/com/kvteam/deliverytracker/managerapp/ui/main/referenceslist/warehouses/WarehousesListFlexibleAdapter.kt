package com.kvteam.deliverytracker.managerapp.ui.main.referenceslist.warehouses

import com.kvteam.deliverytracker.core.models.Warehouse
import com.kvteam.deliverytracker.core.ui.BaseListFlexibleAdapter
import com.kvteam.deliverytracker.core.ui.IBaseListItemActions

class WarehousesListFlexibleAdapter(referenceActions: IBaseListItemActions<WarehouseListItem>)
    : BaseListFlexibleAdapter<Warehouse, WarehouseListItem, WarehouseListItem.WarehousesListViewHolder>(referenceActions)

