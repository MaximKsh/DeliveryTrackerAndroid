package com.kvteam.deliverytracker.core.models

import java.util.*

data class Invitation(
        var id: UUID? = null,
        var invitationCode: String?,
        var creatorId: UUID? = null,
        var created: Date? = null,
        var expires: Date? = null,
        var instanceId: UUID? = null,
        var role: String? = null,
        var preliminaryUser: User? = null)