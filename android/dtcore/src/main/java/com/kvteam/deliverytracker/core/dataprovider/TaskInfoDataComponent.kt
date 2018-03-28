package com.kvteam.deliverytracker.core.dataprovider

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
        private val userDataContainer: UserDataContainer,
        dataContainer: TaskInfoDataContainer
) : BaseDataComponent<TaskInfo, TaskResponse>(dataContainer) {
    override suspend fun createRequestAsync(entity: TaskInfo): NetworkResult<TaskResponse> {
        return taskWebservice.createAsync(entity)
    }

    override suspend fun editRequestAsync(entity: TaskInfo): NetworkResult<TaskResponse> {
        return taskWebservice.editAsync(entity)
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

        users.forEach { userDataContainer.putEntry(it.value) }

        taskInfo.taskProducts
                .map { references.filter { p -> UUID.fromString(p.key) == it.productId!!}.values.first() }
                .map {
                    val p = Product()
                    p.fromMap(it)
                    p
                }
                .forEach { productsDataContainer.putEntry(it) }

        if(taskInfo.paymentTypeId != null) {
            val pt = PaymentType()
            pt.fromMap(references[taskInfo.paymentTypeId.toString()]!!)
            paymentTypeDataContainer.putEntry(pt)
        }

        if(taskInfo.warehouseId != null) {
            val pt = Warehouse()
            pt.fromMap(references[taskInfo.warehouseId.toString()]!!)
            warehouseDataContainer.putEntry(pt)
        }

        if(taskInfo.clientId != null) {
            val pt = Client()
            pt.fromMap(references[taskInfo.clientId.toString()]!!)
            clientDataContainer.putEntry(pt)
        }

        return taskInfo
    }

    override fun entryFactory() = TaskInfo()

}