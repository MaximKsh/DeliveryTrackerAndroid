package com.kvteam.deliverytracker.core.dataprovider.base

import com.kvteam.deliverytracker.core.common.buildDefaultGson
import com.kvteam.deliverytracker.core.models.ViewDigest
import com.kvteam.deliverytracker.core.storage.IStorage
import org.joda.time.DateTime

class ViewDigestContainer : IViewDigestContainer {

    private val viewDigests = mutableMapOf<String, Map<String, ViewDigest>>()

    private val viewsTimes = mutableMapOf<String, DateTime>()

    override fun getViewDigest(viewGroup: String): Map<String, ViewDigest>? {
        val view = viewDigests[viewGroup] ?: return null
        val time = viewsTimes[viewGroup]
        if (time == null) {
            viewsTimes[viewGroup] = DateTime.now()
            return view
        }
        if (time.plusSeconds(CACHE_EXPIRATION_SECONDS).isBeforeNow) {
            viewsTimes.remove(viewGroup)
            viewDigests.remove(viewGroup)
            return null
        }
        return view
    }

    override fun putViewDigest(viewGroup: String, value: Map<String, ViewDigest>) {
        viewDigests[viewGroup] = value
        viewsTimes[viewGroup] = DateTime.now()
    }

    override fun removeViewDigest(viewGroup: String) {
        viewDigests.remove(viewGroup)
        viewsTimes.remove(viewGroup)
    }

    override fun clearViewDigests() {
        viewDigests.clear()
        viewsTimes.clear()
    }

    override fun fromStorage(storage: IStorage) {
    }

    override fun toStorage(storage: IStorage) {
    }

    override fun recycle() {
        val overdueViews = viewsTimes
                .filter { it.value.plusSeconds(CACHE_EXPIRATION_SECONDS).isBeforeNow  }
                .map { it.key }
                .toTypedArray()
        for(view in overdueViews) {
            viewDigests.remove(view)
            viewsTimes.remove(view)
        }
    }

    companion object {
        @JvmStatic
        val gson = buildDefaultGson()
    }
}