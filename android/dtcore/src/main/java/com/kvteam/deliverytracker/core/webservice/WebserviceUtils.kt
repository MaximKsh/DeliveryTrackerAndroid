package com.kvteam.deliverytracker.core.webservice

import android.util.Log
import com.kvteam.deliverytracker.core.session.ISession

fun doubleTimeRequest(
        session: ISession,
        withToken: Boolean,
        doRequestFunc: (Map<String, String>) -> RawNetworkResult) : RawNetworkResult {
    var authHeaders: Map<String, String>?
    if(withToken) {
        authHeaders = getAuthorizationHeaders(session)
        if(authHeaders == null) {
            return RawNetworkResult()
        }
    } else {
        authHeaders = mapOf()
    }
    var result: RawNetworkResult
    try {
        result = doRequestFunc(authHeaders)
    } catch (e: Exception) {
        Log.e("doubleTimeRequest", e.message, e)
        return RawNetworkResult()
    }
    // Если дали токен, но сервер вернул forbidden, может быть токен просрочен
    if(withToken && result.statusCode == INVALID_TOKEN_HTTP_STATUS) {
        session.invalidateToken()
        authHeaders = getAuthorizationHeaders(session) ?: return RawNetworkResult()
        try {
            result = doRequestFunc(authHeaders)
        } catch (e: Exception) {
            Log.e("doubleTimeRequest", e.message, e)
            return RawNetworkResult()
        }
    }
    return result
}

private fun  getAuthorizationHeaders(session: ISession) : Map<String, String>? {
    try {
        val token: String? = session.getToken() ?: return null
        return mapOf("Authorization" to "Bearer $token")
    } catch (e: Exception) {
        Log.e("getAuthorizationHeaders", e.message, e)
        return null
    }
}