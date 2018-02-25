package com.kvteam.deliverytracker.core.models

import com.google.gson.annotations.SerializedName
import org.joda.time.DateTime
import java.util.*

class Invitation : ModelBase() {

    @SerializedName("InvitationCode")
    var invitationCode: String? = null

    @SerializedName("CreatorId")
    var creatorId: UUID? = null

    @SerializedName("Created")
    var created: DateTime? = null

    @SerializedName("Expires")
    var expires: DateTime? = null

    @SerializedName("Role")
    var role: UUID? = null

    @SerializedName("PreliminaryUser")
    var preliminaryUser: User? = null

    override fun fromMap(map: Map<*, *>) {
        super.fromMap(map)

        invitationCode = map["InvitationCode"] as? String
        creatorId = deserializeUUIDFromMap("CreatorId", map)
        created = deserializeDateTimeFromMap("Created", map)
        expires = deserializeDateTimeFromMap("Expires", map)
        role = deserializeUUIDFromMap("Role", map)
        preliminaryUser = deserializeObjectFromMap("PreliminaryUser", map, {User()})
    }
}