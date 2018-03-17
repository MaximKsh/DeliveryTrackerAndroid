package com.kvteam.deliverytracker.core.webservice

import com.google.gson.reflect.TypeToken
import com.kvteam.deliverytracker.core.models.ModelBase
import com.kvteam.deliverytracker.core.webservice.viewmodels.ReferenceRequest
import com.kvteam.deliverytracker.core.webservice.viewmodels.ReferenceResponse
import java.util.*

class ReferenceWebservice (private val webservice: IWebservice) : IReferenceWebservice {

    private val referenceBaseUrl = "/api/reference"

    override suspend  fun getTypesAsync(): NetworkResult<ReferenceResponse> {
        val result = webservice.getAsync<ReferenceResponse>(
                "$referenceBaseUrl/types",
                ReferenceResponse::class.java,
                true)
        return result
    }

    override suspend fun createAsync(type: String, entity: ModelBase)
            : NetworkResult<ReferenceResponse> {
        val request = ReferenceRequest()
        request.entity = entity
        val result = webservice.postAsync<ReferenceResponse>(
                "$referenceBaseUrl/$type/create",
                request,
                object : TypeToken<ReferenceResponse>(){}.type,
                true)
        return result
    }

    override suspend  fun getAsync(type: String, id: UUID):
            NetworkResult<ReferenceResponse> {
        val result = webservice.getAsync<ReferenceResponse>(
                "$referenceBaseUrl/$type/get?id=$id",
                object : TypeToken<ReferenceResponse>(){}.type,
                true)
        return result
    }

    override suspend fun editAsync(type: String, id: UUID, newData: ModelBase):
            NetworkResult<ReferenceResponse> {
        val request = ReferenceRequest()
        newData.id = id
        request.entity = newData
        val result = webservice.postAsync<ReferenceResponse>(
                "$referenceBaseUrl/$type/edit",
                request,
                object : TypeToken<ReferenceResponse>(){}.type,
                true)
        return result
    }

    override suspend fun deleteAsync(type: String, id: UUID):
            NetworkResult<ReferenceResponse> {
        val request = ReferenceRequest()
        request.id = id
        val result = webservice.postAsync<ReferenceResponse>(
                "$referenceBaseUrl/$type/delete",
                request,
                object : TypeToken<ReferenceResponse>(){}.type,
                true)
        return result
    }

}