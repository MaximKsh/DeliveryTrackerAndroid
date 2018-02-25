package com.kvteam.deliverytracker.core.webservice

import com.google.gson.reflect.TypeToken
import com.kvteam.deliverytracker.core.models.ModelBase
import com.kvteam.deliverytracker.core.webservice.viewmodels.ReferenceRequest
import com.kvteam.deliverytracker.core.webservice.viewmodels.ReferenceResponse
import java.util.*

class ReferenceWebservice (private val webservice: IWebservice) : IReferenceWebservice {

    private val referenceBaseUrl = "/api/reference"

    override fun getTypes(): NetworkResult<ReferenceResponse> {
        val result = webservice.get<ReferenceResponse>(
                "$referenceBaseUrl/types",
                ReferenceResponse::class.java,
                true)
        return result
    }

    override fun  create(type: String, entity: ModelBase)
            : NetworkResult<ReferenceResponse> {
        val request = ReferenceRequest()
        request.entity = entity
        val result = webservice.post<ReferenceResponse>(
                "$referenceBaseUrl/$type/create",
                request,
                object : TypeToken<ReferenceResponse>(){}.type,
                true)
        return result
    }

    override fun get(type: String, id: UUID):
            NetworkResult<ReferenceResponse> {
        val result = webservice.get<ReferenceResponse>(
                "$referenceBaseUrl/$type/get?id=$id",
                object : TypeToken<ReferenceResponse>(){}.type,
                true)
        return result
    }

    override fun edit(type: String, id: UUID, newData: ModelBase):
            NetworkResult<ReferenceResponse> {
        val request = ReferenceRequest()
        newData.id = id
        request.entity = newData
        val result = webservice.post<ReferenceResponse>(
                "$referenceBaseUrl/$type/edit",
                request,
                object : TypeToken<ReferenceResponse>(){}.type,
                true)
        return result
    }

    override fun delete(type: String, id: UUID):
            NetworkResult<ReferenceResponse> {
        val request = ReferenceRequest()
        request.id = id
        val result = webservice.post<ReferenceResponse>(
                "$referenceBaseUrl/$type/delete",
                request,
                object : TypeToken<ReferenceResponse>(){}.type,
                true)
        return result
    }

}