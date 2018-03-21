package com.kvteam.deliverytracker.core.dataprovider

import com.kvteam.deliverytracker.core.common.deepCopy
import com.kvteam.deliverytracker.core.models.TaskStateTransition
import java.util.*

class TaskStateTransitionDataComponent(
        private val dataContainer: TaskStateTransitionDataContainer
) : IDataComponent<TaskStateTransition> {
    override suspend fun upsertAsync(entity: TaskStateTransition) {
        throw ActionNotSupportedException()
    }

    override suspend fun getAsync(id: UUID, mode: DataProviderGetMode): TaskStateTransition {
        if(mode != DataProviderGetMode.FORCE_CACHE) {
            throw ActionNotSupportedException()
        }
        val cleanEntity = dataContainer.getEntry(id)
        return cleanEntity?.deepCopy() ?: throw CacheException()
    }

    override suspend fun deleteAsync(id: UUID) {
        throw ActionNotSupportedException()
    }

    override fun invalidate(id: UUID?) {
        if(id == null) {
            dataContainer.clearEntries()
            dataContainer.clearDirties()
        } else {
            dataContainer.removeEntry(id)
            dataContainer.removeDirty(id)
        }
    }

}