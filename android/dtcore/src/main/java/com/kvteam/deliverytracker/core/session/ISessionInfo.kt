package com.kvteam.deliverytracker.core.session

import com.kvteam.deliverytracker.core.roles.Role

interface ISessionInfo {
    val accountType: String
    val authToken: String
    val allowRoles: List<Role>
}