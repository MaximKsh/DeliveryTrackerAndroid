package com.kvteam.deliverytracker.core.webservice

import com.kvteam.deliverytracker.core.models.CodePassword
import com.kvteam.deliverytracker.core.models.Device
import com.kvteam.deliverytracker.core.models.Instance
import com.kvteam.deliverytracker.core.models.User
import com.kvteam.deliverytracker.core.webservice.viewmodels.InstanceRequest
import com.kvteam.deliverytracker.core.webservice.viewmodels.InstanceResponse

class InstanceWebservice (
        private val webservice: IWebservice
): IInstanceWebservice {

    private val instanceBaseUrl = "/api/instance"

    override suspend fun createAsync(
            instance: Instance,
            creator: User,
            creatorDevice: Device,
            codePassword: CodePassword) : NetworkResult<InstanceResponse> {
        if(instance.name == null){
            throw IllegalArgumentException("instance.name")
        }
        if (creator.surname == null) {
            throw IllegalArgumentException("creator.surname")
        }
        if (creator.name == null) {
            throw IllegalArgumentException("creator.name")
        }
        if (creator.patronymic == null) {
            throw IllegalArgumentException("creator.patronymic")
        }
        if (creator.phoneNumber == null) {
            throw IllegalArgumentException("creator.phoneNumber")
        }
        if (codePassword.password == null) {
            throw IllegalArgumentException("codePassword.password")
        }

        val request = InstanceRequest()
        request.instance = instance
        request.creator = creator
        request.creatorDevice = creatorDevice
        request.codePassword = codePassword

        return webservice.postAsync(
                "$instanceBaseUrl/create",
                request,
                InstanceResponse::class.java,
                false)
    }

    override suspend fun getAsync() : NetworkResult<InstanceResponse> {
        return webservice.getAsync(
                "$instanceBaseUrl/get",
                InstanceResponse::class.java,
                true)
    }
}