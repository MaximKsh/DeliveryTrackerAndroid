package com.kvteam.deliverytracker.core.webservice

import com.kvteam.deliverytracker.core.models.ModelBase
import com.kvteam.deliverytracker.core.webservice.viewmodels.ReferenceResponse
import java.util.*

interface IReferenceWebservice {

    fun getTypes() : NetworkResult<ReferenceResponse>

    fun create(type: String, entity: ModelBase)
            : NetworkResult<ReferenceResponse>
    fun edit(type: String, id: UUID, newData: ModelBase)
            : NetworkResult<ReferenceResponse>
    fun get(type: String, id: UUID)
            : NetworkResult<ReferenceResponse>
    fun delete(type: String, id: UUID)
            : NetworkResult<ReferenceResponse>

}