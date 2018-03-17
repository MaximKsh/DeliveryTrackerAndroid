package com.kvteam.deliverytracker.core.webservice

import com.kvteam.deliverytracker.core.models.ModelBase
import com.kvteam.deliverytracker.core.webservice.viewmodels.ReferenceResponse
import java.util.*

interface IReferenceWebservice {

    suspend fun getTypesAsync() : NetworkResult<ReferenceResponse>

    suspend fun createAsync(type: String, entity: ModelBase)
            : NetworkResult<ReferenceResponse>
    suspend fun editAsync(type: String, id: UUID, newData: ModelBase)
            : NetworkResult<ReferenceResponse>
    suspend fun getAsync(type: String, id: UUID)
            : NetworkResult<ReferenceResponse>
    suspend fun deleteAsync(type: String, id: UUID)
            : NetworkResult<ReferenceResponse>

}