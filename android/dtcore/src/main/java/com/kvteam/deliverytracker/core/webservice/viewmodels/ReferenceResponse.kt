package com.kvteam.deliverytracker.core.webservice.viewmodels

import com.kvteam.deliverytracker.core.models.ReferenceDescription

data class ReferenceResponse(
        var entity: Map<String, Any?>? = null,
        var referencesList: Map<String, ReferenceDescription>? = null
) : ResponseBase()