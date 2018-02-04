package com.kvteam.deliverytracker.core.webservice.viewmodels

import com.kvteam.deliverytracker.core.models.ReferenceEntityBase
import java.util.*

data class ReferenceRequest(
    var id : UUID? = null,
    var entity: ReferenceEntityBase? = null
) : RequestBase()