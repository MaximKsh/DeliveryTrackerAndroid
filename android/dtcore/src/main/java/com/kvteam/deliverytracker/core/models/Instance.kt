package com.kvteam.deliverytracker.core.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.util.*

data class Instance(
        @SerializedName("Id", alternate = ["id"])
        var id: UUID? = null,
        @SerializedName("Name", alternate = ["name"])
        var name: String? = null,
        @SerializedName("CreatorId", alternate = ["creatorId"])
        var creatorId: UUID? = null
) : Serializable, IMapDeserializable {


    override fun fromMap(map: Map<*, *>) {
        id = deserializeUUIDFromMap("Id", map)
        name = map["Name"] as? String
        creatorId = deserializeUUIDFromMap("CreatorId", map)
    }
}