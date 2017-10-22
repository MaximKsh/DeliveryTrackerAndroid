package com.kvteam.deliverytracker.core.session

fun  getAuthorizationHeaders(session: ISession) : Map<String, String>? {
    val token: String? = session.getToken() ?: return null
    return mapOf("Authorization" to "Bearer $token")
}