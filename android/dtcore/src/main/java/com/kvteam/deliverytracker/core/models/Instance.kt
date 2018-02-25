package com.kvteam.deliverytracker.core.models

import com.google.gson.annotations.SerializedName
import java.util.*

class Instance : IMapDeserializable {

    @SerializedName("Id")
    var id: UUID? = null

    @SerializedName("Name")
    var name: String? = null

    @SerializedName("CreatorId")
    var creatorId: UUID? = null

    override fun fromMap(map: Map<*, *>) {
        id = deserializeUUIDFromMap("Id", map)
        name = map["Name"] as? String
        creatorId = deserializeUUIDFromMap("CreatorId", map)
    }
}