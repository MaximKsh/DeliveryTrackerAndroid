package com.kvteam.deliverytracker.core.dataprovider.base

import com.kvteam.deliverytracker.core.models.TaskStatisticsItem
import com.kvteam.deliverytracker.core.storage.IStorable
import com.kvteam.deliverytracker.core.storage.IStorage
import org.joda.time.DateTime
import org.joda.time.DateTimeZone

class StatisticsContainer : IStatisticsContainer, IStorable {

    private val container = mutableMapOf<StatisticsKey, List<TaskStatisticsItem>>()

    private val times = mutableMapOf<StatisticsKey, DateTime>()


    override fun get(key: StatisticsKey): List<TaskStatisticsItem>? {
        val item = container[key] ?: return null

        val time = times[key]
        if (time == null) {
            times[key] = DateTime.now(DateTimeZone.UTC)
            return item
        }

        if (time.isBeforeNow) {
            times.remove(key)
            container.remove(key)
            return null
        }
        return item
    }

    override fun put(key: StatisticsKey, statistics: List<TaskStatisticsItem>) {
        container[key] = statistics
        times[key] = DateTime.now(DateTimeZone.UTC).plusSeconds(
                STATISTICS_CACHE_EXPIRATION_SECONDS)
    }

    override fun remove(key: StatisticsKey?) {
        if(key != null) {
            times.remove(key)
            container.remove(key)
        } else {
            times.clear()
            container.clear()
        }
    }

    override fun fromStorage(storage: IStorage) {
    }

    override fun toStorage(storage: IStorage) {
    }
}