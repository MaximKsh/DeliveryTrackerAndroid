package com.kvteam.deliverytracker.core.dataprovider.base

import com.kvteam.deliverytracker.core.models.CollectionModelBase
import com.kvteam.deliverytracker.core.storage.IStorable
import java.util.*

interface ICollectionDataContainer <T : CollectionModelBase>  : IStorable {
    fun getEntry(id: UUID): T?
    fun getByParent(parentId: UUID) : List<T>
    fun putEntry(value: T)
    fun removeEntry(id: UUID)
    fun removeEntriesByParent(parentId: UUID)
    fun clearEntries()

    fun getDirty(id: UUID): T?
    fun getDirtiesByParent(parentId: UUID): List<T>
    fun putDirty(value: T)
    fun removeDirty(id: UUID)
    fun removeDirtiesByParent(parentId: UUID)
    fun clearDirties()
}