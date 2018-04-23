package com.kvteam.deliverytracker.core.webservice.viewmodels

import com.kvteam.deliverytracker.core.models.RequestReferencePackage
import java.util.*

data class ReferenceRequest(
    var id : UUID? = null,
    var entity: RequestReferencePackage? = null
) : RequestBase()