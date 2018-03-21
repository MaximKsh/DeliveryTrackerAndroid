package com.kvteam.deliverytracker.core.dataprovider

import com.kvteam.deliverytracker.core.storage.IStorable
import com.kvteam.deliverytracker.core.storage.IStorage

class DataProvider (
       val viewDigest: ViewDigestComponent,
       val clients: ClientDataComponent,
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
       val taskStateTransitions: TaskStateTransitionDataComponent,
       private val clientContainer: ClientDataContainer,
       private val paymentTypesContainer: PaymentTypeDataContainer,
       private val productsContainer: ProductDataContainer,
       private val warehousesContainer: WarehouseDataContainer,
       private val userContainer: UserDataContainer,
       private val invitationContainer: InvitationDataContainer,
       private val taskInfoContainer: TaskInfoDataContainer,
       private val taskStateContainer: TaskStateTransitionDataContainer) : IStorable {


    override fun fromStorage(storage: IStorage) {
        clientContainer.fromStorage(storage)
        paymentTypesContainer.fromStorage(storage)
        productsContainer.fromStorage(storage)
        warehousesContainer.fromStorage(storage)
        userContainer.fromStorage(storage)
        invitationContainer.fromStorage(storage)
        taskInfoContainer.fromStorage(storage)
        taskStateContainer.fromStorage(storage)
    }

    override fun toStorage(storage: IStorage) {
        clientContainer.toStorage(storage)
        paymentTypesContainer.toStorage(storage)
        productsContainer.toStorage(storage)
        warehousesContainer.toStorage(storage)
        userContainer.toStorage(storage)
        invitationContainer.toStorage(storage)
        taskInfoContainer.toStorage(storage)
        taskStateContainer.toStorage(storage)
    }
}