package com.kvteam.deliverytracker.core.dataprovider

import com.kvteam.deliverytracker.core.common.getDifference
import com.kvteam.deliverytracker.core.dataprovider.base.BaseDataComponent
import com.kvteam.deliverytracker.core.models.*
import com.kvteam.deliverytracker.core.webservice.IReferenceWebservice
import com.kvteam.deliverytracker.core.webservice.NetworkResult
import com.kvteam.deliverytracker.core.webservice.viewmodels.ReferenceResponse
import java.util.*

class ClientDataComponent (
        private val referenceWebservice: IReferenceWebservice,
        clientsContainer: ClientDataContainer,
        private val clientAddressDataContainer: ClientAddressDataContainer
) : BaseDataComponent<Client, ReferenceResponse>(clientsContainer) {
    private val CLIENT = Client::class.java.simpleName

    override fun clearCollectionEntries(id: UUID?) {
        if (id != null) {
            clientAddressDataContainer.removeEntriesByParent(id)
            clientAddressDataContainer.removeDirtiesByParent(id)
        } else {
            clientAddressDataContainer.clearEntries()
            clientAddressDataContainer.clearDirties()
        }
    }

    override suspend fun createRequestAsync(entity: Client): NetworkResult<ReferenceResponse> {
        val pack = RequestReferencePackage(entity)
        pack.collections.addAll(
                clientAddressDataContainer.getDirtiesByParent(entity.id!!)
                        .filter { it.action != CollectionEntityAction.None })
        return referenceWebservice.createAsync(CLIENT, pack)
    }

    override suspend fun editRequestAsync(entity: Client): NetworkResult<ReferenceResponse> {
        val pack = RequestReferencePackage(entity)
        val dirties = clientAddressDataContainer.getDirtiesByParent(entity.id!!)
        val toPackage = mutableListOf<CollectionModelBase>()
        for (dirty in dirties) {
            @Suppress("NON_EXHAUSTIVE_WHEN")
            when(dirty.action) {
                CollectionEntityAction.Create -> {
                    toPackage.add(dirty)
                }
                CollectionEntityAction.Edit -> {
                    val clean = clientAddressDataContainer.getEntry(dirty.id!!)
                    val diff = clean?.getDifference(dirty, { ClientAddress() })
                    if (diff != null) {
                        toPackage.add(diff)
                    }
                }
                CollectionEntityAction.Delete -> {
                    val diff = ClientAddress()
                    diff.id = dirty.id
                    diff.instanceId = dirty.instanceId
                    diff.parentId = dirty.parentId
                    diff.action = CollectionEntityAction.Delete
                    toPackage.add(diff)
                }
            }
        }

        pack.collections.addAll(toPackage)
        return referenceWebservice.editAsync(CLIENT, pack)
    }

    override suspend fun getRequestAsync(id: UUID): NetworkResult<ReferenceResponse> {
        return referenceWebservice.getAsync(CLIENT, id)
    }

    override suspend fun deleteRequestAsync(id: UUID): NetworkResult<ReferenceResponse> {
        return referenceWebservice.deleteAsync(CLIENT, id)
    }

    override fun transformRequestToEntry(result: NetworkResult<ReferenceResponse>): Client {
        val pack = ResponseReferencePackage()
        pack.fromMap(result.entity?.entity!!)

        val client = entryFactory()
        client.fromMap(pack.entry)

        for(serialized in pack.collections) {
            val ca = ClientAddress()
            ca.fromMap(serialized)
            clientAddressDataContainer.putEntry(ca)
        }

        return client
    }

    override fun entryFactory(): Client {
        return Client()
    }

}
