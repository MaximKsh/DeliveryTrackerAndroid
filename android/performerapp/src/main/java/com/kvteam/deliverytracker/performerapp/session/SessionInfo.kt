package com.kvteam.deliverytracker.performerapp.session

import com.kvteam.deliverytracker.core.session.ISessionInfo

class SessionInfo: ISessionInfo {
    override val accountType: String = "com.kvteam.deliverytracker.performerapp"
    override val authToken: String = "full_access"

}