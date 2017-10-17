package com.kvteam.deliverytracker.core.instance

import com.kvteam.deliverytracker.core.webservice.CreateGroupModel
import com.kvteam.deliverytracker.core.webservice.IWebservice
import com.kvteam.deliverytracker.core.webservice.UserInfoModel


class InstanceManager
constructor(var webservice: IWebservice)
    : IInstanceManager {

    override fun create(
            groupName: String,
            creatorDisplayableName: String,
            creatorPassword: String): UserInfoModel? {
        val result = this.webservice.post<UserInfoModel>(
                "/api/group/create",
                CreateGroupModel(groupName, creatorDisplayableName, creatorPassword),
                UserInfoModel::class.java)
        return result.responseEntity
    }


}