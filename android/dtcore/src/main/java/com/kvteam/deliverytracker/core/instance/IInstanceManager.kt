package com.kvteam.deliverytracker.core.instance

import com.kvteam.deliverytracker.core.models.CredentialsModel
import com.kvteam.deliverytracker.core.models.InstanceModel
import com.kvteam.deliverytracker.core.models.UserModel

interface IInstanceManager {
    fun create(
            instance: InstanceModel,
            user: UserModel,
            credentials: CredentialsModel): UserModel?
}