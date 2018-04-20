package com.kvteam.deliverytracker.core.dataprovider.base

import com.kvteam.deliverytracker.core.models.ModelBase
import java.util.*

interface IDataComponent <T : ModelBase> {
    suspend fun upsertAsync(entity: T) : T

    fun get(id: UUID, mode: DataProviderGetMode) : DataProviderGetResult<T>

    suspend fun getAsync(id: UUID, mode: DataProviderGetMode) : DataProviderGetResult<T>

    suspend fun deleteAsync(id: UUID)

    fun hasDirty(id: UUID) : Boolean

    fun invalidate(id: UUID? = null)

    fun invalidateDirty(id: UUID?)
}