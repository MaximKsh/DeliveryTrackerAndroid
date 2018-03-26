package com.kvteam.deliverytracker.core.models

import java.util.*

data class Device(
        var userId: UUID? = null,
        var type: String? = null,
        var version: String? = null,
        var applicationType: String? = null,
        var applicationVersion: String? = null,
        var language: String? = null,
        var firebaseId: String? = null)