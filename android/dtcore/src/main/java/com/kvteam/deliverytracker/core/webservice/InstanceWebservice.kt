package com.kvteam.deliverytracker.core.webservice

import com.kvteam.deliverytracker.core.models.CodePassword
import com.kvteam.deliverytracker.core.models.Instance
import com.kvteam.deliverytracker.core.models.User
import com.kvteam.deliverytracker.core.webservice.viewmodels.InstanceRequest
import com.kvteam.deliverytracker.core.webservice.viewmodels.InstanceResponse

class InstanceWebservice (
        private val webservice: IWebservice
): IInstanceWebservice {

    private val instanceBaseUrl = "/api/instance"

    override fun create(
            instance: Instance,
            creator: User,
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
        request.codePassword = codePassword

        val result = webservice.post<InstanceResponse>(
                "$instanceBaseUrl/create",
                request,
                InstanceResponse::class.java,
                false)
        return result
    }

    override fun get() : NetworkResult<InstanceResponse> {
        return webservice.get(
                "$instanceBaseUrl/get",
                InstanceResponse::class.java,
                true)
    }



}