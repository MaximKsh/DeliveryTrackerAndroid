package com.kvteam.deliverytracker.core.dataprovider

import com.kvteam.deliverytracker.core.common.DifferenceResult
import com.kvteam.deliverytracker.core.common.getDifference
import com.kvteam.deliverytracker.core.dataprovider.base.ActionNotSupportedException
import com.kvteam.deliverytracker.core.dataprovider.base.BaseDataComponent
import com.kvteam.deliverytracker.core.dataprovider.base.IDataContainer
import com.kvteam.deliverytracker.core.dataprovider.base.IViewDigestContainer
import com.kvteam.deliverytracker.core.models.*
import com.kvteam.deliverytracker.core.webservice.ITaskWebservice
import com.kvteam.deliverytracker.core.webservice.NetworkResult
import com.kvteam.deliverytracker.core.webservice.viewmodels.TaskResponse
import java.util.*

class TaskInfoDataComponent (
        private val taskWebservice: ITaskWebservice,
        private val productsDataContainer: IDataContainer<Product>,
        private val paymentTypeDataContainer: IDataContainer<PaymentType>,
        private val warehouseDataContainer: IDataContainer<Warehouse>,
        private val clientDataContainer: IDataContainer<Client>,
        private val clientAddressDataContainer: ClientAddressDataContainer,
        private val userDataContainer: UserDataContainer,
        private val taskProductDataContainer: TaskProductDataContainer,
        dataContainer: TaskInfoDataContainer,
        viewDigestContainer: IViewDigestContainer
) : BaseDataComponent<TaskInfo, TaskResponse>(dataContainer, viewDigestContainer) {

    override fun clearCollections(id: UUID?) {
        if (id != null) {
            taskProductDataContainer.removeEntriesByParent(id)
            taskProductDataContainer.removeDirtiesByParent(id)
        } else {
            taskProductDataContainer.clearEntries()
            taskProductDataContainer.clearDirties()
        }
    }

    override fun clearCollectionDirties(id: UUID?) {
        if (id != null) {
            taskProductDataContainer.removeDirtiesByParent(id)
        } else {
            taskProductDataContainer.clearDirties()
        }
    }

    override suspend fun createRequestAsync(entity: TaskInfo): NetworkResult<TaskResponse> {
        val taskPackage = TaskPackage()
        taskPackage.taskInfo.add(entity)
        val taskProducts = taskProductDataContainer.getDirtiesByParent(entity.id!!)
                .filter { it.action != CollectionEntityAction.None }

        taskPackage.taskProducts.addAll(taskProducts)
        return taskWebservice.createAsync(taskPackage)
    }

    override suspend fun editRequestAsync(diff: DifferenceResult<TaskInfo>): NetworkResult<TaskResponse>? {
        val entity = diff.difference
        var hasDifference = diff.hasDifferentFields

        val pack = TaskPackage()
        pack.taskInfo.add(entity)

        val dirties = taskProductDataContainer.getDirtiesByParent(entity.id!!)
        val toPackage = mutableListOf<TaskProduct>()
        for (dirty in dirties) {
            @Suppress("NON_EXHAUSTIVE_WHEN")
            when(dirty.action) {
                CollectionEntityAction.Create -> {
                    toPackage.add(dirty)
                    hasDifference = true
                }
                CollectionEntityAction.Edit -> {
                    val clean = taskProductDataContainer.getEntry(dirty.id!!)
                    val tpDiff = clean?.getDifference(dirty, { TaskProduct() })
                    if (tpDiff?.hasDifferentFields == true) {
                        toPackage.add(tpDiff.difference)
                        hasDifference = true
                    }
                }
                CollectionEntityAction.Delete -> {
                    dirty.quantity = 0
                    toPackage.add(dirty)
                    hasDifference = true
                }
            }
        }

        pack.taskProducts.addAll(toPackage)
        return if (hasDifference) {
            taskWebservice.editAsync(pack)
        } else {
            null
        }
    }

    override suspend fun getRequestAsync(id: UUID): NetworkResult<TaskResponse> {
        return taskWebservice.getAsync(id)
    }

    override suspend fun deleteRequestAsync(id: UUID): NetworkResult<TaskResponse> {
        throw ActionNotSupportedException()
    }

    override fun transformRequestToEntry(result: NetworkResult<TaskResponse>): TaskInfo {
        val taskInfo = result.entity?.taskPackage?.taskInfo?.first()!!
        val references = result.entity?.taskPackage?.linkedReferences!!
        val users = result.entity?.taskPackage?.linkedUsers!!
        val products = result.entity?.taskPackage?.taskProducts!!

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

            for(serialized in pack.collections) {
                val ca = ClientAddress()
                ca.fromMap(serialized)
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

        return taskInfo
    }

    override fun entryFactory() = TaskInfo()

}