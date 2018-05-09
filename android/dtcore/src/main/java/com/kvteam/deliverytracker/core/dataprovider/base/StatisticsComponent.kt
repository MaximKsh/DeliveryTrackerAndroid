package com.kvteam.deliverytracker.core.dataprovider.base

import com.kvteam.deliverytracker.core.common.StatisticsViewGroup
import com.kvteam.deliverytracker.core.models.TaskStatisticsItem
import com.kvteam.deliverytracker.core.roles.Role
import com.kvteam.deliverytracker.core.webservice.IViewWebservice
import java.util.*

class StatisticsComponent(
        private val statisticsContainer: StatisticsContainer,
        private val viewWebservice: IViewWebservice
) : IStatisticsComponent {
    override fun get(
            userId: UUID,
            userRole: Role,
            viewName: String,
            mode: DataProviderGetMode): List<TaskStatisticsItem>? {
        return when(mode) {
            DataProviderGetMode.FORCE_WEB -> throw ActionNotSupportedException()
            DataProviderGetMode.FORCE_CACHE -> getForceCache(userId, userRole, viewName)
            DataProviderGetMode.PREFER_WEB -> throw ActionNotSupportedException()
            DataProviderGetMode.PREFER_CACHE -> throw ActionNotSupportedException()
            DataProviderGetMode.DIRTY -> throw ActionNotSupportedException()
        }
    }

    override suspend fun getAsync(
            userId: UUID,
            userRole: Role,
            viewName: String,
            mode: DataProviderGetMode): List<TaskStatisticsItem>? {
        return when(mode) {
            DataProviderGetMode.FORCE_WEB -> getForceWebAsync(userId, userRole, viewName)
            DataProviderGetMode.FORCE_CACHE -> getForceCache(userId, userRole, viewName)
            DataProviderGetMode.PREFER_WEB -> getPreferWebAsync(userId, userRole, viewName)
            DataProviderGetMode.PREFER_CACHE -> getPreferCacheAsync(userId, userRole, viewName)
            DataProviderGetMode.DIRTY -> throw ActionNotSupportedException()
        }
    }

    override fun invalidate(userId: UUID, userRole: Role, viewName: String) {
        statisticsContainer.remove(StatisticsKey(userId, userRole, viewName))
    }

    override fun invalidate() {
        statisticsContainer.remove()
    }

    private suspend fun getForceWebAsync(
            userId: UUID,
            userRole: Role,
            viewName: String) : List<TaskStatisticsItem> {
        val key = if (userRole == Role.Performer) {
            "performer_id"
        } else {
            "author_id"
        }

        val viewResult = viewWebservice.getViewResultAsync(
                StatisticsViewGroup,
                viewName,
                mapOf(key to userId))
        if(!viewResult.success) {
            throw NetworkException(viewResult)
        }

        val entities = viewResult.entity?.viewResult!!
        val statisticItems = entities.map {
            val item = TaskStatisticsItem()
            item.fromMap(it)
            return@map item
        }
        statisticsContainer.put(StatisticsKey(userId, userRole, viewName), statisticItems)
        return statisticItems
    }
    private fun getForceCache(
            userId: UUID,
            userRole: Role,
            viewName: String) : List<TaskStatisticsItem> {
        return statisticsContainer.get(StatisticsKey(userId, userRole, viewName)) ?: throw CacheException()

    }

    private suspend fun getPreferWebAsync(
            userId: UUID,
            userRole: Role,
            viewName: String) : List<TaskStatisticsItem>{
        return try {
            getForceWebAsync(userId, userRole, viewName)
        } catch (e: NetworkException) {
            getForceCache(userId, userRole, viewName)
        }
    }

    private suspend fun getPreferCacheAsync(
            userId: UUID,
            userRole: Role,
            viewName: String) : List<TaskStatisticsItem> {
        return try {
            getForceCache(userId, userRole, viewName)
        } catch (e: CacheException) {
            getForceWebAsync(userId, userRole, viewName)
        }
    }

}