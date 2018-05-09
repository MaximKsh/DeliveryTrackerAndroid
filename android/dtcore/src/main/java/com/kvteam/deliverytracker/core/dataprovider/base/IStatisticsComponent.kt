package com.kvteam.deliverytracker.core.dataprovider.base

import com.kvteam.deliverytracker.core.models.TaskStatisticsItem
import com.kvteam.deliverytracker.core.roles.Role
import java.util.*

interface IStatisticsComponent {
    fun get (userId: UUID,
             userRole: Role,
             viewName: String,
             mode: DataProviderGetMode = DataProviderGetMode.FORCE_CACHE) : List<TaskStatisticsItem>?

    suspend fun getAsync (userId: UUID,
                          userRole: Role,
                          viewName: String,
                          mode: DataProviderGetMode) : List<TaskStatisticsItem>?

    fun invalidate (userId: UUID,
                    userRole: Role,
                    viewName: String)

    fun invalidate ()
}