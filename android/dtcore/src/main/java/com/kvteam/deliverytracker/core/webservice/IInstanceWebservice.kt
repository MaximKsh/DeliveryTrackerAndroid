package com.kvteam.deliverytracker.core.webservice

import com.kvteam.deliverytracker.core.models.CodePassword
import com.kvteam.deliverytracker.core.models.Device
import com.kvteam.deliverytracker.core.models.Instance
import com.kvteam.deliverytracker.core.models.User
import com.kvteam.deliverytracker.core.webservice.viewmodels.InstanceResponse

interface IInstanceWebservice {
    suspend fun createAsync(instance: Instance,
                            creator: User,
                            creatorDevice: Device,
                            codePassword: CodePassword): NetworkResult<InstanceResponse>

    suspend fun getAsync(): NetworkResult<InstanceResponse>
}