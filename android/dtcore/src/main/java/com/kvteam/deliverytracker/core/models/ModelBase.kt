package com.kvteam.deliverytracker.core.models

import com.google.gson.annotations.SerializedName
import com.kvteam.deliverytracker.core.common.MoveAlways
import java.util.*

open class ModelBase() : IMapDeserializable {
    @SerializedName("Id", alternate = ["id"])
    @MoveAlways
    var id: UUID? = null
    @SerializedName("InstanceId", alternate = ["instanceId"])
    @MoveAlways
    var instanceId: UUID? = null
    @SerializedName("Type", alternate = ["type"])
    @MoveAlways
    var type: String = this.javaClass.simpleName

    override fun fromMap(map: Map<*, *>) {
        id = deserializeUUIDFromMap("Id", map)
        instanceId = deserializeUUIDFromMap("InstanceId", map)
        val type = map["Type"] as? String
        if(type != null) {
            this.type = type
        }
    }
}