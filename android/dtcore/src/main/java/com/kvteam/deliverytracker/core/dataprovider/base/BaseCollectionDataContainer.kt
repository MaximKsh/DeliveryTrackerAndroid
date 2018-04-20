package com.kvteam.deliverytracker.core.dataprovider.base

import com.kvteam.deliverytracker.core.common.buildDefaultGson
import com.kvteam.deliverytracker.core.models.CollectionModelBase
import com.kvteam.deliverytracker.core.storage.IStorage
import java.util.*

abstract class BaseCollectionDataContainer <T : CollectionModelBase> : ICollectionDataContainer<T> {
    private val entries = mutableMapOf<UUID, T>()
    private val dirties = mutableMapOf<UUID, T>()

    protected abstract val storageKey: String

    override fun getEntry(id: UUID) : T? {
        return entries[id]
    }

    override fun getByParent(parentId: UUID): List<T> {
        return entries
                .filter { it.value.parentId == parentId }
                .map { it.value }
                .toList()
    }

    override fun putEntry(value: T) {
        val id = value.id!!
        entries[id] = value
    }

    override fun removeEntry(id: UUID) {
        entries.remove(id)
    }

    override fun removeEntriesByParent(parentId: UUID) {
        val toDelete = entries
                .filter { it.value.parentId == parentId }
                .map { it.value }
        for (td in toDelete) {
            entries.remove(td.id)
        }
    }

    override fun clearEntries() {
        entries.clear()
    }

    override fun getDirty(id: UUID) : T? {
        return dirties[id]
    }

    override fun getDirtiesByParent(parentId: UUID): List<T> {
        return dirties
                .filter { it.value.parentId == parentId }
                .map { it.value }
                .toList()
    }

    override fun putDirty(value: T) {
        val id = value.id!!
        dirties[id] = value
    }

    override fun removeDirty(id: UUID) {
        dirties.remove(id)
    }

    override fun removeDirtiesByParent(parentId: UUID) {
        val toDelete = dirties
                .filter { it.value.parentId == parentId }
                .map { it.value }
        for (td in toDelete) {
            dirties.remove(td.id)
        }
    }

    override fun clearDirties() {
        dirties.clear()
    }


    override fun fromStorage(storage: IStorage) {
    }

    override fun toStorage(storage: IStorage) {
    }

    companion object {
        @JvmStatic
        val gson = buildDefaultGson()
    }
}