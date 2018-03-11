package com.kvteam.deliverytracker.core.models

import com.google.gson.annotations.SerializedName
import org.joda.time.DateTime
import java.io.Serializable
import java.util.*

data class Invitation(@SerializedName("InvitationCode", alternate = ["invitationCode"])
                      var invitationCode: String? = null,
                      @SerializedName("CreatorId", alternate = ["creatorId"])
                      var creatorId: UUID? = null,
                      @SerializedName("Created", alternate = ["created"])
                      var created: DateTime? = null,
                      @SerializedName("Expires", alternate = ["expires"])
                      var expires: DateTime? = null,
                      @SerializedName("Role", alternate = ["role"])
                      var role: UUID? = null,
                      @SerializedName("PreliminaryUser", alternate = ["preliminaryUser"])
                      var preliminaryUser: User? = null) : ModelBase(), Serializable {
    override fun fromMap(map: Map<*, *>) {
        super.fromMap(map)

        invitationCode = map["InvitationCode"] as? String
        creatorId = deserializeUUIDFromMap("CreatorId", map)
        created = deserializeDateTimeFromMap("Created", map)
        expires = deserializeDateTimeFromMap("Expires", map)
        role = deserializeUUIDFromMap("Role", map)
        preliminaryUser = deserializeObjectFromMap("PreliminaryUser", map, { User() })
    }
}