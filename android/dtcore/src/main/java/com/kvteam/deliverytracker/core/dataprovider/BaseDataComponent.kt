package com.kvteam.deliverytracker.core.dataprovider

import com.kvteam.deliverytracker.core.common.deepCopy
import com.kvteam.deliverytracker.core.common.getDifference
import com.kvteam.deliverytracker.core.models.ModelBase
import com.kvteam.deliverytracker.core.webservice.NetworkResult
import com.kvteam.deliverytracker.core.webservice.viewmodels.ResponseBase
import kotlinx.coroutines.experimental.async
import java.util.*

abstract class BaseDataComponent <T : ModelBase, R : ResponseBase>(
    private val dataContainer: IDataContainer<T>
) : IDataComponent<T> {

    abstract suspend fun createRequestAsync(entity: T): NetworkResult<R>
    abstract suspend fun editRequestAsync(entity: T): NetworkResult<R>
    abstract suspend fun getRequestAsync(id: UUID): NetworkResult<R>
    abstract suspend fun deleteRequestAsync(id: UUID): NetworkResult<R>

    abstract fun transformRequestToEntry(result: NetworkResult<R>): T
    abstract fun entryFactory() : T

    override suspend fun upsertAsync(entity: T): Unit = async {
        val origin = dataContainer.getEntry(entity.id!!)
        val result =  if (origin != null) {
            val diff = origin.getDifference(entity, ::entryFactory)
            editRequestAsync(diff)
        } else {
            createRequestAsync(entity)
        }

        val newEntity = transformRequestToEntry(result)
        dataContainer.putEntry(newEntity)
        dataContainer.removeDirty(entity.id!!)
        return@async
    }.await()

    override suspend fun getAsync(id: UUID, mode: DataProviderGetMode): DataProviderGetResult<T> = async {
        when(mode) {
            DataProviderGetMode.FORCE_WEB -> getForceWebAsync(id)
            DataProviderGetMode.FORCE_CACHE -> getForceCache(id)
            DataProviderGetMode.PREFER_WEB -> getPreferWebAsync(id)
            DataProviderGetMode.PREFER_CACHE -> getPreferCacheAsync(id)
            DataProviderGetMode.DIRTY -> throw ActionNotSupportedException()
        }
    }.await()

    override suspend fun deleteAsync(id: UUID): Unit = async {
        val result = deleteRequestAsync(id)
        if (!result.success) {
            throw NetworkException(result)
        }
        invalidate(id)
    }.await()

    override fun invalidate(id: UUID?) {
        if(id == null) {
            dataContainer.clearEntries()
            dataContainer.clearDirties()
        } else {
            dataContainer.removeEntry(id)
            dataContainer.removeDirty(id)
        }
    }

    private suspend fun getForceWebAsync(id: UUID) : DataProviderGetResult<T> = async {
        val result = getRequestAsync(id)
        if(result.success) {
            val entry = transformRequestToEntry(result)
            dataContainer.putEntry(entry)
            return@async DataProviderGetResult(entry.deepCopy(), DataProviderGetOrigin.WEB)
        }
        throw NetworkException(result)
    }.await()

    private fun getForceCache(id: UUID) : DataProviderGetResult<T> {
        val cleanEntity = dataContainer.getEntry(id)
        if(cleanEntity != null) {
            return DataProviderGetResult(cleanEntity.deepCopy(), DataProviderGetOrigin.CACHE)
        }
        throw CacheException()
    }

    private suspend fun getPreferWebAsync(id: UUID) : DataProviderGetResult<T> {
        return try {
            getForceWebAsync(id)
        } catch (e: NetworkException) {
            getForceCache(id)
        }
    }

    private suspend fun getPreferCacheAsync(id: UUID) : DataProviderGetResult<T> {
        return try {
            getForceCache(id)
        } catch (e: Exception) {
            getForceWebAsync(id)
        }
    }
    private fun getDirty(id: UUID): T {
        val dirtyEntity = dataContainer.getDirty(id)
        if(dirtyEntity != null) {
            return dirtyEntity
        }
        val cleanEntity = dataContainer.getEntry(id)
        if(cleanEntity != null) {
            val copy = cleanEntity.deepCopy()
            dataContainer.putDirty(copy)
            return copy
        }

        val client = entryFactory()
        client.id = id
        dataContainer.putDirty(client)
        return client
    }

}
