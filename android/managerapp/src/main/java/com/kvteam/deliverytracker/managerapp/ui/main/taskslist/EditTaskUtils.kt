package com.kvteam.deliverytracker.managerapp.ui.main.taskslist

import com.kvteam.deliverytracker.core.common.PaymentTypesView
import com.kvteam.deliverytracker.core.common.ReferenceViewGroup
import com.kvteam.deliverytracker.core.common.WarehousesView
import com.kvteam.deliverytracker.core.dataprovider.base.DataProvider
import com.kvteam.deliverytracker.core.dataprovider.base.DataProviderGetMode
import com.kvteam.deliverytracker.core.dataprovider.base.NetworkException
import com.kvteam.deliverytracker.core.models.PaymentType
import com.kvteam.deliverytracker.core.models.Warehouse


internal suspend fun loadPaymentTypes (dp: DataProvider) : List<PaymentType> {
    val paymentTypesIds = dp.paymentTypesViews
            .getViewResultAsync(
                    ReferenceViewGroup,
                    PaymentTypesView,
                    mode = DataProviderGetMode.PREFER_CACHE)
            .viewResult

    if (paymentTypesIds.isEmpty()) {
        return listOf()
    }

    val paymentTypes = mutableListOf<PaymentType>()
    for (id in paymentTypesIds) {
        try {
            val (e, _) = dp.paymentTypes.getAsync(id, DataProviderGetMode.PREFER_CACHE)
            paymentTypes.add(e)
        } catch (e: NetworkException) {
            return listOf()
        }
    }
    return paymentTypes
}

internal suspend fun loadWarehouses(dp: DataProvider) : List<Warehouse> {
    val warehousesIds = dp.warehousesViews
            .getViewResultAsync(
                    ReferenceViewGroup,
                    WarehousesView,
                    mode = DataProviderGetMode.PREFER_CACHE)
            .viewResult

    if (warehousesIds.isEmpty()) {
        return listOf()
    }

    val warehouses = mutableListOf<Warehouse>()
    for (id in warehousesIds) {
        try {
            val (e, _) = dp.warehouses.getAsync(id, DataProviderGetMode.PREFER_CACHE)
            warehouses.add(e)
        } catch (e: NetworkException) {
            return listOf()
        }
    }
    return warehouses
}