package com.kvteam.deliverytracker.core.dataprovider

import com.kvteam.deliverytracker.core.common.buildDefaultGson
import com.kvteam.deliverytracker.core.models.ModelBase
import com.kvteam.deliverytracker.core.storage.IStorage
import org.joda.time.DateTime
import java.util.*

abstract  class BaseDataContainer <T : ModelBase> : IDataContainer<T> {
    private val entries = mutableMapOf<UUID, T>()
    private val dirties = mutableMapOf<UUID, T>()
    private val views = mutableMapOf<ViewRequestKey, List<UUID>>()

    private val entriesTimes = mutableMapOf<UUID, DateTime>()
    private val viewsTimes = mutableMapOf<ViewRequestKey, DateTime>()

    protected abstract val storageKey: String

    override fun getEntry(id: UUID) : T? {
        val entry = entries[id] ?: return null
        val time = entriesTimes[id]
        if (time == null) {
            entriesTimes[id] = DateTime.now()
            return entry
        }
        if (time.plusSeconds(CACHE_EXPIRATION_SECONDS).isBeforeNow) {
            entriesTimes.remove(id)
            entries.remove(id)
            return null
        }
        return entry
    }

    override fun putEntry(value: T) {
        val id = value.id!!
        entries[id] = value
        entriesTimes[id] = DateTime.now()
    }

    override fun removeEntry(id: UUID) {
        entriesTimes.remove(id)
        entries.remove(id)    }

    override fun clearEntries() {
        entriesTimes.clear()
        entries.clear()
    }

    override fun getDirty(id: UUID) : T? {
        return dirties[id]
    }

    override fun putDirty(value: T) {
        val id = value.id!!
        dirties[id] = value
    }

    override fun removeDirty(id: UUID) {
        dirties.remove(id)
    }

    override fun clearDirties() {
        dirties.clear()    }

    override fun getView(vrk: ViewRequestKey): List<UUID>? {
        val view = views[vrk] ?: return null
        val time = viewsTimes[vrk]
        if (time == null) {
            viewsTimes[vrk] = DateTime.now()
            return view
        }
        if (time.plusSeconds(CACHE_EXPIRATION_SECONDS).isBeforeNow) {
            viewsTimes.remove(vrk)
            views.remove(vrk)
            return null
        }
        return view
    }

    override fun putView(vrk: ViewRequestKey, value: List<UUID>) {
        views[vrk] = value
        viewsTimes[vrk] = DateTime.now()
    }

    override fun removeView(vrk: ViewRequestKey) {
        views.remove(vrk)
        viewsTimes.remove(vrk)
    }

    override fun clearViews() {
        views.clear()
        viewsTimes.clear()
    }

    override fun fromStorage(storage: IStorage) {
    }

    override fun toStorage(storage: IStorage) {
    }

    override fun recycle() {
        val overdueEntries = entriesTimes
                .filter { it.value.plusSeconds(CACHE_EXPIRATION_SECONDS).isBeforeNow  }
                .map { it.key }
                .toTypedArray()
        for(entry in overdueEntries) {
            entries.remove(entry)
            entriesTimes.remove(entry)
        }

        val overdueViews = viewsTimes
                .filter { it.value.plusSeconds(CACHE_EXPIRATION_SECONDS).isBeforeNow  }
                .map { it.key }
                .toTypedArray()
        for(view in overdueViews) {
            views.remove(view)
            viewsTimes.remove(view)
        }
    }

    companion object {
        @JvmStatic
        val gson = buildDefaultGson()
    }
}