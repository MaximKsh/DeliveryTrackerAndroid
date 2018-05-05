package com.kvteam.deliverytracker.core.webservice.viewmodels

import com.kvteam.deliverytracker.core.models.CodePassword
import com.kvteam.deliverytracker.core.models.Device
import com.kvteam.deliverytracker.core.models.Instance
import com.kvteam.deliverytracker.core.models.User

data class InstanceRequest(
    var instance: Instance? = null,
    var creator: User? = null,
    var creatorDevice: Device? = null,
    var codePassword: CodePassword? = null
): RequestBase()