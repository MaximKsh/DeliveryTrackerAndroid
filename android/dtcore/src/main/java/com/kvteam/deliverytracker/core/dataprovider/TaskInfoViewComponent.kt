package com.kvteam.deliverytracker.core.dataprovider

import com.kvteam.deliverytracker.core.dataprovider.base.BaseViewComponent
import com.kvteam.deliverytracker.core.models.*
import com.kvteam.deliverytracker.core.webservice.IViewWebservice
import com.kvteam.deliverytracker.core.webservice.NetworkResult
import com.kvteam.deliverytracker.core.webservice.viewmodels.ViewResponse

class TaskInfoViewComponent (
        viewWebservice: IViewWebservice,
        dataContainer: TaskInfoDataContainer,
        private val userDataContainer: UserDataContainer,
        private val paymentTypeDataContainer: PaymentTypeDataContainer,
        private val warehouseDataContainer: WarehouseDataContainer,
        private val clientAddressDataContainer: ClientAddressDataContainer,
        private val clientDataContainer: ClientDataContainer,
        private val productsDataContainer: ProductDataContainer,
        private val taskProductDataContainer: TaskProductDataContainer
) : BaseViewComponent<TaskInfo>(viewWebservice, dataContainer) {
    override fun entryFactory() = TaskInfo()

    override fun transformViewResultToEntries(viewResult: NetworkResult<ViewResponse>): List<TaskInfo> {
        val serialized = viewResult.entity?.viewResult!!

        if (serialized.isEmpty()) {
            return listOf()
        }

        val taskInfos = mutableListOf<TaskInfo>()
        for (item in serialized) {
            val taskPackage = TaskPackage()
            taskPackage.fromMap(item)
            taskInfos.addAll(taskPackage.taskInfo)

            for (taskInfo in taskPackage.taskInfo) {
                val references = taskPackage.linkedReferences
                val users = taskPackage.linkedUsers
                val products = taskPackage.taskProducts

                users.forEach { userDataContainer.putEntry(it.value) }

                if(taskInfo.paymentTypeId != null) {
                    val pack = ResponseReferencePackage()
                    pack.fromMap(references[taskInfo.paymentTypeId.toString()]!!)
                    val pt = PaymentType()
                    pt.fromMap(pack.entry)
                    paymentTypeDataContainer.putEntry(pt)
                }

                if(taskInfo.warehouseId != null) {
                    val pack = ResponseReferencePackage()
                    pack.fromMap(references[taskInfo.warehouseId.toString()]!!)
                    val pt = Warehouse()
                    pt.fromMap(pack.entry)
                    warehouseDataContainer.putEntry(pt)
                }

                if(taskInfo.clientId != null) {
                    val pack = ResponseReferencePackage()
                    pack.fromMap(references[taskInfo.clientId.toString()]!!)

                    val client = Client()
                    client.fromMap(pack.entry)

                    for(ser in pack.collections) {
                        val ca = ClientAddress()
                        ca.fromMap(ser)
                        clientAddressDataContainer.putEntry(ca)
                    }
                    clientDataContainer.putEntry(client)
                }

                products.forEach {
                    val pack = ResponseReferencePackage()
                    pack.fromMap(references[it.productId.toString()]!!)
                    val pt = Product()
                    pt.fromMap(pack.entry)
                    productsDataContainer.putEntry(pt)

                    taskProductDataContainer.putEntry(it)
                }
            }
        }
        return taskInfos
    }
}