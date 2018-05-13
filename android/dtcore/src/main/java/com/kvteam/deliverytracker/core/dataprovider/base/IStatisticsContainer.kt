package com.kvteam.deliverytracker.core.dataprovider.base

import com.kvteam.deliverytracker.core.models.TaskStatisticsItem

interface IStatisticsContainer {
    fun get(key: StatisticsKey): List<TaskStatisticsItem>?

    fun put (key: StatisticsKey, statistics: List<TaskStatisticsItem>)

    fun remove(key: StatisticsKey? = null)
}