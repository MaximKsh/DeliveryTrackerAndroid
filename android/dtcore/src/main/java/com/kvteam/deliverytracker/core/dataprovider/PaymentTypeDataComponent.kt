package com.kvteam.deliverytracker.core.dataprovider

import com.kvteam.deliverytracker.core.models.PaymentType
import com.kvteam.deliverytracker.core.webservice.IReferenceWebservice
import com.kvteam.deliverytracker.core.webservice.NetworkResult
import com.kvteam.deliverytracker.core.webservice.viewmodels.ReferenceResponse
import java.util.*

class PaymentTypeDataComponent(
        private val referenceWebservice: IReferenceWebservice,
        dataContainer: PaymentTypeDataContainer
): BaseDataComponent<PaymentType, ReferenceResponse>(dataContainer) {
    private val PAYMENT_TYPE = PaymentType::class.java.simpleName

    override suspend fun createRequestAsync(entity: PaymentType): NetworkResult<ReferenceResponse> {
        return referenceWebservice.createAsync(PAYMENT_TYPE, entity)
    }

    override suspend fun editRequestAsync(entity: PaymentType): NetworkResult<ReferenceResponse> {
        return referenceWebservice.editAsync(PAYMENT_TYPE, entity.id!!, entity)
    }

    override suspend fun getRequestAsync(id: UUID): NetworkResult<ReferenceResponse> {
        return referenceWebservice.getAsync(PAYMENT_TYPE, id)
    }

    override suspend fun deleteRequestAsync(id: UUID): NetworkResult<ReferenceResponse> {
        return referenceWebservice.deleteAsync(PAYMENT_TYPE, id)
    }

    override fun transformRequestToEntry(result: NetworkResult<ReferenceResponse>): PaymentType {
        val pt = entryFactory()
        pt.fromMap(result.entity?.entity!!)
        return pt
    }

    override fun entryFactory(): PaymentType = PaymentType()

}