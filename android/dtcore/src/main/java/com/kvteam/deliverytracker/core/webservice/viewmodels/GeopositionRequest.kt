package com.kvteam.deliverytracker.core.webservice.viewmodels

import com.kvteam.deliverytracker.core.models.Geoposition

data class GeopositionRequest(
        var geoposition: Geoposition? = null
): RequestBase()