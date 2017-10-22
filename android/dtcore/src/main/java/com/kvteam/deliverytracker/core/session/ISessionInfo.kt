package com.kvteam.deliverytracker.core.session

interface ISessionInfo {
    val accountType: String
    val authToken: String
    val allowRoles: List<String>
}