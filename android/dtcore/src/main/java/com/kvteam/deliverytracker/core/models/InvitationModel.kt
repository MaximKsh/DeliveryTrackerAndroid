package com.kvteam.deliverytracker.core.models

import java.util.*

data class InvitationModel(
        var invitationCode: String,
        var expirationDate: Date,
        var preliminaryUser: UserModel)