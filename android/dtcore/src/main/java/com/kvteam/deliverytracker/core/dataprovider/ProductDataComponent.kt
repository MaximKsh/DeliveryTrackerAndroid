package com.kvteam.deliverytracker.core.dataprovider

import com.kvteam.deliverytracker.core.common.DifferenceResult
import com.kvteam.deliverytracker.core.common.ProductType
import com.kvteam.deliverytracker.core.dataprovider.base.BaseDataComponent
import com.kvteam.deliverytracker.core.dataprovider.base.IViewDigestContainer
import com.kvteam.deliverytracker.core.models.Product
import com.kvteam.deliverytracker.core.models.RequestReferencePackage
import com.kvteam.deliverytracker.core.models.ResponseReferencePackage
import com.kvteam.deliverytracker.core.webservice.IReferenceWebservice
import com.kvteam.deliverytracker.core.webservice.NetworkResult
import com.kvteam.deliverytracker.core.webservice.viewmodels.ReferenceResponse
import java.util.*

class ProductDataComponent (
        private val referenceWebservice: IReferenceWebservice,
        dataContainer: ProductDataContainer,
        viewDigestContainer: IViewDigestContainer
) : BaseDataComponent<Product, ReferenceResponse>(dataContainer, viewDigestContainer) {
    override suspend fun createRequestAsync(entity: Product): NetworkResult<ReferenceResponse> {
        return referenceWebservice.createAsync(ProductType, RequestReferencePackage(entity))
    }

    override suspend fun editRequestAsync(diff: DifferenceResult<Product>): NetworkResult<ReferenceResponse>? {
        return if (diff.hasDifferentFields) {
            referenceWebservice.editAsync(ProductType, RequestReferencePackage(diff.difference))
        } else {
            null
        }
    }

    override suspend fun getRequestAsync(id: UUID): NetworkResult<ReferenceResponse> {
        return referenceWebservice.getAsync(ProductType, id)
    }

    override suspend fun deleteRequestAsync(id: UUID): NetworkResult<ReferenceResponse> {
        return referenceWebservice.deleteAsync(ProductType, id)
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