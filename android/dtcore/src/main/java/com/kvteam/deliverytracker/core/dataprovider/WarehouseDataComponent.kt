package com.kvteam.deliverytracker.core.dataprovider

import com.kvteam.deliverytracker.core.models.Warehouse
import com.kvteam.deliverytracker.core.webservice.IReferenceWebservice
import com.kvteam.deliverytracker.core.webservice.NetworkResult
import com.kvteam.deliverytracker.core.webservice.viewmodels.ReferenceResponse
import java.util.*
class WarehouseDataComponent(
        private val referenceWebservice: IReferenceWebservice,
        dataContainer: WarehouseDataContainer
) : BaseDataComponent<Warehouse, ReferenceResponse> (dataContainer) {
    private val WAREHOUSE = Warehouse::class.java.simpleName

    override suspend fun createRequestAsync(entity: Warehouse): NetworkResult<ReferenceResponse> {
        return referenceWebservice.createAsync(WAREHOUSE, entity)
    }

    override suspend fun editRequestAsync(entity: Warehouse): NetworkResult<ReferenceResponse> {
        return referenceWebservice.editAsync(WAREHOUSE, entity.id!!, entity)
    }

    override suspend fun getRequestAsync(id: UUID): NetworkResult<ReferenceResponse> {
        return referenceWebservice.getAsync(WAREHOUSE, id)
    }

    override suspend fun deleteRequestAsync(id: UUID): NetworkResult<ReferenceResponse> {
        return referenceWebservice.deleteAsync(WAREHOUSE, id)
    }

    override fun transformRequestToEntry(result: NetworkResult<ReferenceResponse>): Warehouse {
        val pt = entryFactory()
        pt.fromMap(result.entity?.entity!!)
        return pt
    }

    override fun entryFactory(): Warehouse = Warehouse()
}