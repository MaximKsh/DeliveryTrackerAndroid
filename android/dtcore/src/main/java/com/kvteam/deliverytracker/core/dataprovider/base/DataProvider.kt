package com.kvteam.deliverytracker.core.dataprovider.base

import com.kvteam.deliverytracker.core.dataprovider.*
import com.kvteam.deliverytracker.core.storage.IStorable
import com.kvteam.deliverytracker.core.storage.IStorage

class DataProvider (
        val viewDigest: ViewDigestComponent,
        val clients: ClientDataComponent,
        val clientAddresses: ClientAddressDataComponent,
        val clientsViews: ClientViewComponent,
        val paymentTypes: PaymentTypeDataComponent,
        val paymentTypesViews: PaymentTypeViewComponent,
        val products: ProductDataComponent,
        val productsViews: ProductViewComponent,
        val warehouses: WarehouseDataComponent,
        val warehousesViews: WarehouseViewComponent,
        val users: UserDataComponent,
        val userViews: UserViewComponent,
        val invitations: InvitationDataComponent,
        val invitationView: InvitationViewComponent,
        val taskInfos: TaskInfoDataComponent,
        val taskInfoViews: TaskInfoViewComponent,
        val taskProducts: TaskProductDataComponent,
        val statistics: StatisticsComponent,
        private val clientContainer: ClientDataContainer,
        private val clientAddressesContainer: ClientAddressDataContainer,
        private val paymentTypesContainer: PaymentTypeDataContainer,
        private val productsContainer: ProductDataContainer,
        private val warehousesContainer: WarehouseDataContainer,
        private val userContainer: UserDataContainer,
        private val invitationContainer: InvitationDataContainer,
        private val taskInfoContainer: TaskInfoDataContainer,
        private val taskProductDataContainer: TaskProductDataContainer,
        private val statisticsContainer: StatisticsContainer) : IStorable {


    override fun fromStorage(storage: IStorage) {
        clientContainer.fromStorage(storage)
        clientAddressesContainer.fromStorage(storage)
        paymentTypesContainer.fromStorage(storage)
        productsContainer.fromStorage(storage)
        warehousesContainer.fromStorage(storage)
        userContainer.fromStorage(storage)
        invitationContainer.fromStorage(storage)
        taskInfoContainer.fromStorage(storage)
        taskProductDataContainer.fromStorage(storage)
        statisticsContainer.fromStorage(storage)
    }

    override fun toStorage(storage: IStorage) {
        clientContainer.toStorage(storage)
        clientAddressesContainer.toStorage(storage)
        paymentTypesContainer.toStorage(storage)
        productsContainer.toStorage(storage)
        warehousesContainer.toStorage(storage)
        userContainer.toStorage(storage)
        invitationContainer.toStorage(storage)
        taskInfoContainer.toStorage(storage)
        taskProductDataContainer.toStorage(storage)
        statisticsContainer.toStorage(storage)
    }
}