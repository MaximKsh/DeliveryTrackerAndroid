package com.kvteam.deliverytracker.core.models

import com.google.gson.annotations.SerializedName
import java.util.*

open class ModelBase() : IMapDeserializable {
    @SerializedName("Id")
    var id: UUID? = null
    @SerializedName("InstanceId")
    var instanceId: UUID? = null

    override fun fromMap(map: Map<*, *>) {
        id = deserializeUUIDFromMap("Id", map)
        instanceId = deserializeUUIDFromMap("InstanceId", map)
    }
}