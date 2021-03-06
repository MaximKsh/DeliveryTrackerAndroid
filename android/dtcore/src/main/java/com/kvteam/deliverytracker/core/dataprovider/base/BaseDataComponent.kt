package com.kvteam.deliverytracker.core.dataprovider.base

import com.kvteam.deliverytracker.core.common.DifferenceResult
import com.kvteam.deliverytracker.core.common.deepCopy
import com.kvteam.deliverytracker.core.common.getDifference
import com.kvteam.deliverytracker.core.models.ModelBase
import com.kvteam.deliverytracker.core.webservice.NetworkResult
import com.kvteam.deliverytracker.core.webservice.viewmodels.ResponseBase
import kotlinx.coroutines.experimental.async
import java.util.*

abstract class BaseDataComponent <T : ModelBase, R : ResponseBase>(
    private val dataContainer: IDataContainer<T>,
    private val viewDigestContainer: IViewDigestContainer
) : IDataComponent<T> {

    protected abstract suspend fun createRequestAsync(entity: T): NetworkResult<R>
    protected abstract suspend fun editRequestAsync(diff: DifferenceResult<T>): NetworkResult<R>?
    protected abstract suspend fun getRequestAsync(id: UUID): NetworkResult<R>
    protected abstract suspend fun deleteRequestAsync(id: UUID): NetworkResult<R>

    protected abstract fun transformRequestToEntry(result: NetworkResult<R>): T
    protected abstract fun entryFactory() : T

    protected open fun clearCollections(id: UUID? = null) {

    }

    protected open fun clearCollectionDirties (id: UUID? = null) {

    }

    override suspend fun upsertAsync(entity: T): T = async {
        val origin = dataContainer.getEntry(entity.id!!)
        val result =  if (origin != null) {
            val diffResult = origin.getDifference(entity, ::entryFactory)
            editRequestAsync(diffResult)
        } else {
            createRequestAsync(entity)
        }

        if (result == null) {
            // Запроса не было, т.к. изменения данных не было
            // origin есть, т.к. при создании изменения отсутствовать не могут.
            return@async origin!!
        }

        if (!result.success) {
            throw NetworkException(result)
        }

        invalidate(entity.id!!)
        dataContainer.clearViews()
        viewDigestContainer.clearViewDigests()

        val newEntity = transformRequestToEntry(result)
        dataContainer.putEntry(newEntity)
        return@async newEntity
    }.await()

    override fun get(id: UUID, mode: DataProviderGetMode): DataProviderGetResult<T> {
        return when(mode) {
            DataProviderGetMode.FORCE_CACHE -> getForceCache(id)
            DataProviderGetMode.DIRTY -> getDirty(id)
            else -> throw ActionNotSupportedException()
        }
    }

    override suspend fun getAsync(id: UUID, mode: DataProviderGetMode): DataProviderGetResult<T> = async {
        when(mode) {
            DataProviderGetMode.FORCE_WEB -> getForceWebAsync(id)
            DataProviderGetMode.FORCE_CACHE -> getForceCache(id)
            DataProviderGetMode.PREFER_WEB -> getPreferWebAsync(id)
            DataProviderGetMode.PREFER_CACHE -> getPreferCacheAsync(id)
            DataProviderGetMode.DIRTY -> getDirty(id)
        }
    }.await()

    override suspend fun deleteAsync(id: UUID): Unit = async {
        val result = deleteRequestAsync(id)
        if (!result.success) {
            throw NetworkException(result)
        }
        viewDigestContainer.clearViewDigests()
        invalidate(id)
    }.await()

    override fun hasDirty(id: UUID) : Boolean {
        return dataContainer.getDirty(id) != null
    }

    override fun invalidate() {
        dataContainer.clearEntries()
        dataContainer.clearDirties()
        clearCollections()
    }

    override fun invalidate(id: UUID) {
        dataContainer.removeEntry(id)
        dataContainer.removeDirty(id)
        clearCollections(id)
    }

    override fun invalidateDirty() {
        dataContainer.clearDirties()
        clearCollectionDirties()
    }

    override fun invalidateDirty(id: UUID) {
        dataContainer.removeDirty(id)
        clearCollectionDirties(id)
    }

    private suspend fun getForceWebAsync(id: UUID) : DataProviderGetResult<T> = async {
        val result = getRequestAsync(id)
        if(result.success) {
            val entry = transformRequestToEntry(result)
            dataContainer.putEntry(entry)
            return@async DataProviderGetResult(
                    entry.deepCopy(),
                    DataProviderGetOrigin.WEB)
        }
        throw NetworkException(result)
    }.await()

    private fun getForceCache(id: UUID) : DataProviderGetResult<T> {
        val cleanEntity = dataContainer.getEntry(id)
        if(cleanEntity != null) {
            return DataProviderGetResult(
                    cleanEntity.deepCopy(),
                    DataProviderGetOrigin.CACHE)
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
    private fun getDirty(id: UUID): DataProviderGetResult<T> {
        val dirtyEntity = dataContainer.getDirty(id)
        if(dirtyEntity != null) {
            return DataProviderGetResult(dirtyEntity, DataProviderGetOrigin.DIRTY)
        }
        val cleanEntity = dataContainer.getEntry(id)
        if(cleanEntity != null) {
            val copy = cleanEntity.deepCopy()
            dataContainer.putDirty(copy)
            return DataProviderGetResult(copy, DataProviderGetOrigin.DIRTY)
        }

        val entry = entryFactory()
        entry.id = id
        dataContainer.putDirty(entry)
        return DataProviderGetResult(entry, DataProviderGetOrigin.DIRTY)
    }

}
