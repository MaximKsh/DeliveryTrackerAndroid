package com.kvteam.deliverytracker.core.instance

import com.kvteam.deliverytracker.core.webservice.UserInfoModel

/**
 * Created by maxim on 17.10.17.
 */

interface IInstanceManager {
    fun create(
            groupName: String,
            creatorDisplayableName: String,
            creatorPassword: String): UserInfoModel?
}