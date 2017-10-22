package com.kvteam.deliverytracker.core.instance

import com.kvteam.deliverytracker.core.models.CreateInstanceModel
import com.kvteam.deliverytracker.core.models.CredentialsModel
import com.kvteam.deliverytracker.core.models.InstanceModel
import com.kvteam.deliverytracker.core.models.UserModel
import com.kvteam.deliverytracker.core.webservice.*


class InstanceManager
constructor(var webservice: IWebservice)
    : IInstanceManager {

    override fun create(
            instance: InstanceModel,
            user: UserModel,
            credentials: CredentialsModel): UserModel? {
        val requestObj = CreateInstanceModel(instance, credentials, user)
        val result = this.webservice.post<UserModel>(
                "/api/instance/create",
                requestObj,
                UserModel::class.java)
        return result.responseEntity
    }


}