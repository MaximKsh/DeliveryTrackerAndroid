package com.kvteam.deliverytracker.core.dataprovider

import com.kvteam.deliverytracker.core.common.PaymentTypeType
import com.kvteam.deliverytracker.core.dataprovider.base.BaseDataComponent
import com.kvteam.deliverytracker.core.dataprovider.base.IViewDigestContainer
import com.kvteam.deliverytracker.core.models.PaymentType
import com.kvteam.deliverytracker.core.models.RequestReferencePackage
import com.kvteam.deliverytracker.core.models.ResponseReferencePackage
import com.kvteam.deliverytracker.core.webservice.IReferenceWebservice
import com.kvteam.deliverytracker.core.webservice.NetworkResult
import com.kvteam.deliverytracker.core.webservice.viewmodels.ReferenceResponse
import java.util.*

class PaymentTypeDataComponent(
        private val referenceWebservice: IReferenceWebservice,
        dataContainer: PaymentTypeDataContainer,
        viewDigestContainer: IViewDigestContainer
): BaseDataComponent<PaymentType, ReferenceResponse>(dataContainer, viewDigestContainer) {
    override suspend fun createRequestAsync(entity: PaymentType): NetworkResult<ReferenceResponse> {
        return referenceWebservice.createAsync(PaymentTypeType, RequestReferencePackage(entity))
    }

    override suspend fun editRequestAsync(entity: PaymentType): NetworkResult<ReferenceResponse> {
        return referenceWebservice.editAsync(PaymentTypeType, RequestReferencePackage(entity))
    }

    override suspend fun getRequestAsync(id: UUID): NetworkResult<ReferenceResponse> {
        return referenceWebservice.getAsync(PaymentTypeType, id)
    }

    override suspend fun deleteRequestAsync(id: UUID): NetworkResult<ReferenceResponse> {
        return referenceWebservice.deleteAsync(PaymentTypeType, id)
    }

    override fun transformRequestToEntry(result: NetworkResult<ReferenceResponse>): PaymentType {
        val pack = ResponseReferencePackage()
        pack.fromMap(result.entity?.entity!!)

        val pt = entryFactory()
        pt.fromMap(pack.entry)
        return pt
    }

    override fun entryFactory(): PaymentType = PaymentType()

}