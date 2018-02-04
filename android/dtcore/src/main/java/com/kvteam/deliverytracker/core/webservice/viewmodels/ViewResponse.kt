package com.kvteam.deliverytracker.core.webservice.viewmodels

import com.kvteam.deliverytracker.core.models.ViewDigest

data class ViewResponse(
    var groups: List<String>? = null,
    var views: List<String>? = null,
    var digest: Map<String, ViewDigest>? = null,
    var viewResult: List<Map<String, Any?>>? = null
) : ResponseBase()