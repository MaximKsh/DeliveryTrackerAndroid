package com.kvteam.deliverytracker.core.dataprovider

import com.kvteam.deliverytracker.core.dataprovider.base.BaseDataComponent
import com.kvteam.deliverytracker.core.models.Product
import com.kvteam.deliverytracker.core.models.RequestReferencePackage
import com.kvteam.deliverytracker.core.models.ResponseReferencePackage
import com.kvteam.deliverytracker.core.webservice.IReferenceWebservice
import com.kvteam.deliverytracker.core.webservice.NetworkResult
import com.kvteam.deliverytracker.core.webservice.viewmodels.ReferenceResponse
import java.util.*

class ProductDataComponent (
        private val referenceWebservice: IReferenceWebservice,
        dataContainer: ProductDataContainer
) : BaseDataComponent<Product, ReferenceResponse>(dataContainer) {
    private val PRODUCT = Product::class.java.simpleName

    override suspend fun createRequestAsync(entity: Product): NetworkResult<ReferenceResponse> {
        return referenceWebservice.createAsync(PRODUCT, RequestReferencePackage(entity))
    }

    override suspend fun editRequestAsync(entity: Product): NetworkResult<ReferenceResponse> {
        return referenceWebservice.editAsync(PRODUCT, RequestReferencePackage(entity))
    }

    override suspend fun getRequestAsync(id: UUID): NetworkResult<ReferenceResponse> {
        return referenceWebservice.getAsync(PRODUCT, id)
    }

    override suspend fun deleteRequestAsync(id: UUID): NetworkResult<ReferenceResponse> {
        return referenceWebservice.deleteAsync(PRODUCT, id)
    }

    override fun transformRequestToEntry(result: NetworkResult<ReferenceResponse>): Product {
        val pack = ResponseReferencePackage()
        pack.fromMap(result.entity?.entity!!)

        val pt = entryFactory()
        pt.fromMap(pack.entry)
        return pt
    }

    override fun entryFactory(): Product = Product()
}