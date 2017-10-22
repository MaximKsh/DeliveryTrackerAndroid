package com.kvteam.deliverytracker.core.session

const val SETTINGS_CONTEXT = "SETTINGS_CONTEXT"

fun  getAuthorizationHeaders(session: ISession) : Map<String, String>? {
    val token: String? = session.getToken() ?: return null
    return mapOf("Authorization" to "Bearer $token")
}