package com.kvteam.deliverytracker.core.dataprovider.base

import com.kvteam.deliverytracker.core.roles.Role
import java.util.*

data class StatisticsKey (
        val userId: UUID,
        val userRole: Role,
        val viewName: String)