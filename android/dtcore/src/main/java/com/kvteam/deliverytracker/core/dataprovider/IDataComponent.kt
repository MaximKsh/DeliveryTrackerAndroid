package com.kvteam.deliverytracker.core.dataprovider

import com.kvteam.deliverytracker.core.models.ModelBase
import java.util.*

interface IDataComponent <T : ModelBase> {

    suspend fun upsertAsync(entity: T)

    suspend fun getAsync(id: UUID, mode: DataProviderGetMode) : T

    suspend fun deleteAsync(id: UUID)

    fun invalidate(id: UUID? = null)



}