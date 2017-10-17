package com.kvteam.deliverytracker.core.webservice

import java.util.*

/**
 * Created by maxim on 18.10.17.
 */

data class InvitationModel(
        val invitationCode: String,
        val role: String,
        val expirationDate: Date,
        val groupName: String)