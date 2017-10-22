package com.kvteam.deliverytracker.managerapp.session

import com.kvteam.deliverytracker.core.session.ISessionInfo

class SessionInfo: ISessionInfo {
    override val accountType: String = "com.kvteam.deliverytracker.managerapp"
    override val authToken: String = "full_access"

}