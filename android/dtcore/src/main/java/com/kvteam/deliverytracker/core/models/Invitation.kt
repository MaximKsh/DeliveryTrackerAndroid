package com.kvteam.deliverytracker.core.models

import com.google.gson.internal.LinkedTreeMap
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

data class Invitation(
        var id: UUID? = null,
        var invitationCode: String? = null,
        var creatorId: UUID? = null,
        var created: Date? = null,
        var expires: Date? = null,
        var instanceId: UUID? = null,
        var role: UUID? = null,
        var preliminaryUser: User? = null) {

    open fun fromMap(map: Map<*, *>) {
        val idStr = map["Id"] as? String
        if(idStr != null) {
            try {
                id = UUID.fromString(idStr)
            } catch (e: Exception) {
            }
        }

        val instanceIdStr = map["InstanceId"] as? String
        if(instanceIdStr != null) {
            try {
                instanceId = UUID.fromString(instanceIdStr)
            } catch (e: Exception) {
            }
        }

        val creatorIdStr = map["CreatorId"] as? String
        if(creatorIdStr != null) {
            try {
                creatorId = UUID.fromString(creatorIdStr)
            } catch (e: Exception) {
            }
        }

        val roleStr = map["Role"] as? String
        if(roleStr != null) {
            try {
                role = UUID.fromString(roleStr)
            } catch (e: Exception) {
            }
        }

        created = Date()

        expires = Date()

        invitationCode = map["InvitationCode"] as? String
        val userSerialized = map["PreliminaryUser"]
        if(userSerialized is LinkedTreeMap<*, *>) {
            val usr = User()
            usr.fromMap(userSerialized)
            preliminaryUser = usr
        }
    }

}