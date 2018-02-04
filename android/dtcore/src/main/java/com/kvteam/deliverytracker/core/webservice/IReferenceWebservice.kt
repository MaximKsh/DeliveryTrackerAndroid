package com.kvteam.deliverytracker.core.webservice

import com.kvteam.deliverytracker.core.models.ReferenceEntityBase
import com.kvteam.deliverytracker.core.webservice.viewmodels.ReferenceResponse
import java.util.*

interface IReferenceWebservice {

    fun getTypes() : NetworkResult<ReferenceResponse>

    fun create(type: String, entity: ReferenceEntityBase)
            : NetworkResult<ReferenceResponse>
    fun edit(type: String, id: UUID, newData: ReferenceEntityBase)
            : NetworkResult<ReferenceResponse>
    fun get(type: String, id: UUID)
            : NetworkResult<ReferenceResponse>
    fun delete(type: String, id: UUID)
            : NetworkResult<ReferenceResponse>

}