package com.kvteam.deliverytracker.core.webservice


class InstanceManager
constructor(var webservice: IWebservice)
    : IInstanceManager {

    override fun create(
            groupName: String,
            creatorDisplayableName: String,
            creatorPassword: String) {

    }


}