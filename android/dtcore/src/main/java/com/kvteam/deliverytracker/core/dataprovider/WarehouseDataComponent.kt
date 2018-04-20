package com.kvteam.deliverytracker.core.dataprovider

import com.kvteam.deliverytracker.core.common.WarehouseType
import com.kvteam.deliverytracker.core.dataprovider.base.BaseDataComponent
import com.kvteam.deliverytracker.core.dataprovider.base.IViewDigestContainer
import com.kvteam.deliverytracker.core.models.RequestReferencePackage
import com.kvteam.deliverytracker.core.models.ResponseReferencePackage
import com.kvteam.deliverytracker.core.models.Warehouse
import com.kvteam.deliverytracker.core.webservice.IReferenceWebservice
import com.kvteam.deliverytracker.core.webservice.NetworkResult
import com.kvteam.deliverytracker.core.webservice.viewmodels.ReferenceResponse
import java.util.*

class WarehouseDataComponent(
        private val referenceWebservice: IReferenceWebservice,
        dataContainer: WarehouseDataContainer,
        viewDigestContainer: IViewDigestContainer
) : BaseDataComponent<Warehouse, ReferenceResponse>(dataContainer, viewDigestContainer) {
    override suspend fun createRequestAsync(entity: Warehouse): NetworkResult<ReferenceResponse> {
        return referenceWebservice.createAsync(WarehouseType, RequestReferencePackage(entity))
    }

    override suspend fun editRequestAsync(entity: Warehouse): NetworkResult<ReferenceResponse> {
        return referenceWebservice.editAsync(WarehouseType, RequestReferencePackage( entity ))
    }

    override suspend fun getRequestAsync(id: UUID): NetworkResult<ReferenceResponse> {
        return referenceWebservice.getAsync(WarehouseType, id)
    }

    override suspend fun deleteRequestAsync(id: UUID): NetworkResult<ReferenceResponse> {
        return referenceWebservice.deleteAsync(WarehouseType, id)
    }

    override fun transformRequestToEntry(result: NetworkResult<ReferenceResponse>): Warehouse {
        val pack = ResponseReferencePackage()
        pack.fromMap(result.entity?.entity!!)

        val pt = entryFactory()
        pt.fromMap(pack.entry)
        return pt
    }

    override fun entryFactory(): Warehouse = Warehouse()
}