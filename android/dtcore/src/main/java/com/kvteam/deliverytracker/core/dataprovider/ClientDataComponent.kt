package com.kvteam.deliverytracker.core.dataprovider

import com.kvteam.deliverytracker.core.common.ClientType
import com.kvteam.deliverytracker.core.common.DifferenceResult
import com.kvteam.deliverytracker.core.common.getDifference
import com.kvteam.deliverytracker.core.dataprovider.base.BaseDataComponent
import com.kvteam.deliverytracker.core.dataprovider.base.IViewDigestContainer
import com.kvteam.deliverytracker.core.models.*
import com.kvteam.deliverytracker.core.webservice.IReferenceWebservice
import com.kvteam.deliverytracker.core.webservice.NetworkResult
import com.kvteam.deliverytracker.core.webservice.viewmodels.ReferenceResponse
import java.util.*

class ClientDataComponent (
        private val referenceWebservice: IReferenceWebservice,
        clientsContainer: ClientDataContainer,
        viewDigestContainer: IViewDigestContainer,
        private val clientAddressDataContainer: ClientAddressDataContainer
) : BaseDataComponent<Client, ReferenceResponse>(clientsContainer, viewDigestContainer) {
    override fun clearCollections(id: UUID?) {
        if (id != null) {
            clientAddressDataContainer.removeEntriesByParent(id)
            clientAddressDataContainer.removeDirtiesByParent(id)
        } else {
            clientAddressDataContainer.clearEntries()
            clientAddressDataContainer.clearDirties()
        }
    }

    override fun clearCollectionDirties(id: UUID?) {
        if (id != null) {
            clientAddressDataContainer.removeDirtiesByParent(id)
        } else {
            clientAddressDataContainer.clearDirties()
        }
    }

    override suspend fun createRequestAsync(entity: Client): NetworkResult<ReferenceResponse> {
        val pack = RequestReferencePackage(entity)
        val newAddresses = clientAddressDataContainer.getDirtiesByParent(entity.id!!)
                .filter { it.action == CollectionEntityAction.Create }
        pack.collections.addAll(newAddresses)
        return referenceWebservice.createAsync(ClientType, pack)
    }

    override suspend fun editRequestAsync(diff: DifferenceResult<Client>): NetworkResult<ReferenceResponse>? {
        val entity = diff.difference
        var hasDifference = diff.hasDifferentFields

        val pack = RequestReferencePackage(entity)
        val dirties = clientAddressDataContainer.getDirtiesByParent(entity.id!!)
        val toPackage = mutableListOf<CollectionModelBase>()
        for (dirty in dirties) {
            @Suppress("NON_EXHAUSTIVE_WHEN")
            when(dirty.action) {
                CollectionEntityAction.Create -> {
                    toPackage.add(dirty)
                    hasDifference = true
                }
                CollectionEntityAction.Edit -> {
                    val clean = clientAddressDataContainer.getEntry(dirty.id!!)
                    val caDiff = clean?.getDifference(dirty, { ClientAddress() })
                    if (caDiff?.hasDifferentFields == true) {
                        toPackage.add(caDiff.difference)
                        hasDifference = true
                    }
                }
                CollectionEntityAction.Delete -> {
                    val caDiff = ClientAddress()
                    caDiff.id = dirty.id
                    caDiff.instanceId = dirty.instanceId
                    caDiff.parentId = dirty.parentId
                    caDiff.action = CollectionEntityAction.Delete
                    toPackage.add(caDiff)
                    hasDifference = true
                }
            }
        }
        pack.collections.addAll(toPackage)

        return if (hasDifference) {
            referenceWebservice.editAsync(ClientType, pack)
        } else {
            null
        }
    }

    override suspend fun getRequestAsync(id: UUID): NetworkResult<ReferenceResponse> {
        return referenceWebservice.getAsync(ClientType, id)
    }

    override suspend fun deleteRequestAsync(id: UUID): NetworkResult<ReferenceResponse> {
        return referenceWebservice.deleteAsync(ClientType, id)
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
