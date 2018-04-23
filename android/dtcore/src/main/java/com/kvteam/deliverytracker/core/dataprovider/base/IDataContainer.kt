package com.kvteam.deliverytracker.core.dataprovider.base

import com.kvteam.deliverytracker.core.models.ModelBase
import com.kvteam.deliverytracker.core.storage.IStorable
import java.util.*

interface IDataContainer <T : ModelBase> : IStorable {
    fun getEntry(id: UUID): T?
    fun putEntry(value: T)
    fun removeEntry(id: UUID)
    fun clearEntries()

    fun getDirty(id: UUID): T?
    fun putDirty(value: T)
    fun removeDirty(id: UUID)
    fun clearDirties()

    fun getView(vrk: ViewRequestKey): List<UUID>?
    fun putView(vrk: ViewRequestKey, value: List<UUID>)
    fun removeView(vrk: ViewRequestKey)
    fun clearViews()

    fun recycle()
}