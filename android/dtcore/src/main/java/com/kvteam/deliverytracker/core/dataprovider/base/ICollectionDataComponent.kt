package com.kvteam.deliverytracker.core.dataprovider.base

import com.kvteam.deliverytracker.core.models.CollectionModelBase
import java.util.*

interface ICollectionDataComponent  <T : CollectionModelBase> {
    fun saveChanges (entity: T) : T

    fun forgetChanges (id: UUID)

    fun get(id: UUID, parentId: UUID, mode: DataProviderGetMode) : T

    fun getByParent(parentId: UUID, mode: DataProviderGetMode) : List<T>

    fun delete(id: UUID)
}