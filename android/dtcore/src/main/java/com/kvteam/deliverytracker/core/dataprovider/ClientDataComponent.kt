package com.kvteam.deliverytracker.core.dataprovider

import com.kvteam.deliverytracker.core.models.Client
import com.kvteam.deliverytracker.core.webservice.IReferenceWebservice
import com.kvteam.deliverytracker.core.webservice.NetworkResult
import com.kvteam.deliverytracker.core.webservice.viewmodels.ReferenceResponse
import java.util.*

class ClientDataComponent (
        private val referenceWebservice: IReferenceWebservice,
        clientsContainer: ClientDataContainer
) : BaseDataComponent<Client, ReferenceResponse>(clientsContainer) {
    private val CLIENT = Client::class.java.simpleName

    override suspend fun createRequestAsync(entity: Client): NetworkResult<ReferenceResponse> {
        return referenceWebservice.createAsync(CLIENT, entity)
    }

    override suspend fun editRequestAsync(entity: Client): NetworkResult<ReferenceResponse> {
        return referenceWebservice.editAsync(CLIENT, entity.id!!, entity)
    }

    override suspend fun getRequestAsync(id: UUID): NetworkResult<ReferenceResponse> {
        return referenceWebservice.getAsync(CLIENT, id)
    }

    override suspend fun deleteRequestAsync(id: UUID): NetworkResult<ReferenceResponse> {
        return referenceWebservice.deleteAsync(CLIENT, id)
    }

    override fun transformRequestToEntry(result: NetworkResult<ReferenceResponse>): Client {
        val client = entryFactory()
        client.fromMap(result.entity?.entity!!)
        return client
    }

    override fun entryFactory(): Client {
        return Client()
    }

}
