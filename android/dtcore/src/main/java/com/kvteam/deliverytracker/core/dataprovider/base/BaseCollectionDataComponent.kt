package com.kvteam.deliverytracker.core.dataprovider.base

import com.kvteam.deliverytracker.core.common.deepCopy
import com.kvteam.deliverytracker.core.models.CollectionEntityAction
import com.kvteam.deliverytracker.core.models.CollectionModelBase
import java.util.*

abstract class BaseCollectionDataComponent<T : CollectionModelBase>(
        private val collectionDataContainer: ICollectionDataContainer<T>
) : ICollectionDataComponent<T> {

    protected abstract fun entryFactory() : T

    override fun saveChanges(entity: T) : T {
        val origin = collectionDataContainer.getEntry(entity.id!!)
        if (origin != null) {
            entity.action = CollectionEntityAction.Edit
        } else {
            entity.action = CollectionEntityAction.Create
        }
        collectionDataContainer.putDirty(entity)

        return entity
    }

    override fun forgetChanges(id: UUID) {
        collectionDataContainer.removeDirty(id)
    }

    override fun get(id: UUID, parentId: UUID, mode: DataProviderGetMode) : T {
        return when (mode) {
            DataProviderGetMode.FORCE_CACHE -> getForceCache(id, parentId)
            DataProviderGetMode.DIRTY -> getDirty(id, parentId)
            else -> throw ActionNotSupportedException()
        }
    }

    override fun getByParent(parentId: UUID, mode: DataProviderGetMode) : List<T> {
        return when (mode) {
            DataProviderGetMode.FORCE_CACHE -> getForceCacheByParent(parentId)
            DataProviderGetMode.DIRTY -> getDirtiesByParent(parentId)
            else -> throw ActionNotSupportedException()
        }
    }

    override fun delete(id: UUID) {
        val origin = collectionDataContainer.getEntry(id)
        if (origin != null) {
            val copy = origin.deepCopy()
            copy.action = CollectionEntityAction.Delete
            collectionDataContainer.putDirty(copy)
        } else {
            collectionDataContainer.removeDirty(id)
        }
    }

    private fun getForceCache(id: UUID, parentId: UUID) : T {
        val entry =  collectionDataContainer.getEntry(id)
        if (entry == null || entry.parentId != parentId) {
            throw CacheException()
        }
        return entry.deepCopy()
    }


    private fun getDirty(id: UUID, parentId: UUID): T {
        val dirtyEntity = collectionDataContainer.getDirty(id)
        if(dirtyEntity != null) {
            return dirtyEntity
        }
        val cleanEntity = collectionDataContainer.getEntry(id)
        if(cleanEntity != null) {
            val copy = cleanEntity.deepCopy()
            collectionDataContainer.putDirty(copy)
            return copy
        }

        val entry = entryFactory()
        entry.id = id
        entry.parentId = parentId
        collectionDataContainer.putDirty(entry)
        return entry
    }

    private fun getForceCacheByParent(parentId: UUID) : List<T> {
        return collectionDataContainer.getByParent(parentId)
                .map { it.deepCopy() }
                .toList()
    }


    private fun getDirtiesByParent(parentId: UUID): List<T> {
        val dirtyByParent = collectionDataContainer.getDirtiesByParent(parentId)
        val cleanByParent = collectionDataContainer.getByParent(parentId)
        val dirties = mutableListOf<T>()

        for (clean in cleanByParent) {
            val flag = dirtyByParent.any { it.id == clean.id }
            if (!flag) {
                val copy = clean.deepCopy()
                collectionDataContainer.putDirty(copy)
                dirties.add( copy )
            }
        }
        dirties.addAll(dirtyByParent)
        return dirties
    }
}