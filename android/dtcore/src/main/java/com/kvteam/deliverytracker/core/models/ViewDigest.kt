package com.kvteam.deliverytracker.core.models

import java.io.Serializable

data class ViewDigest (
    var caption: String? = null,
    var count: Long? = null,
    var entityType: String? = null,
    var order: Int? = null,
    var iconName: String? = null
) : Serializable