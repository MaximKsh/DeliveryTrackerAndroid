package com.kvteam.deliverytracker.performerapp.session

import com.kvteam.deliverytracker.core.roles.Role
import com.kvteam.deliverytracker.core.session.ISessionInfo

class SessionInfo: ISessionInfo {
    override val accountType = "com.kvteam.deliverytracker.performerapp"
    override val authToken = "full_access"
    override val allowRoles = listOf(Role.Performer)
}