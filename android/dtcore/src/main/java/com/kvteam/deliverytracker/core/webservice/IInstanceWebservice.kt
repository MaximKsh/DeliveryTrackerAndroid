package com.kvteam.deliverytracker.core.webservice

import com.kvteam.deliverytracker.core.models.CodePassword
import com.kvteam.deliverytracker.core.models.Instance
import com.kvteam.deliverytracker.core.models.User
import com.kvteam.deliverytracker.core.webservice.viewmodels.InstanceResponse

interface IInstanceWebservice {
    fun create(instance: Instance,
               creator: User,
               codePassword: CodePassword) : NetworkResult<InstanceResponse>

    fun get() : NetworkResult<InstanceResponse>
}