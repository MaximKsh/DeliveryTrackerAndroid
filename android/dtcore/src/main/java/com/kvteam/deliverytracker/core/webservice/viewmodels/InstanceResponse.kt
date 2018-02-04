package com.kvteam.deliverytracker.core.webservice.viewmodels

import com.kvteam.deliverytracker.core.models.Instance
import com.kvteam.deliverytracker.core.models.User

data class InstanceResponse(
        var instance: Instance? = null,
        var creator: User? = null,
        var token: String? = null
) : ResponseBase()