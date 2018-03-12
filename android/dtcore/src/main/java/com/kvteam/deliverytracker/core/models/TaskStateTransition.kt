package com.kvteam.deliverytracker.core.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.util.*

data class TaskStateTransition(
        @SerializedName("Id", alternate = ["id"])
        var id: UUID? = null,
        @SerializedName("Role", alternate = ["role"])
        var role: UUID? = null,
        @SerializedName("InitialState", alternate = ["initialState"])
        var initialState: UUID? = null,
        @SerializedName("FinalState", alternate = ["finalState"])
        var finalState: UUID? = null,
        @SerializedName("ButtonCaption", alternate = ["buttonCaption"])
        var buttonCaption: String? = null
) : Serializable, IMapDeserializable {
    override fun fromMap(map: Map<*, *>) {
        id = deserializeUUIDFromMap("Id", map)
        role = deserializeUUIDFromMap("Role", map)
        initialState = deserializeUUIDFromMap("InitialState", map)
        finalState = deserializeUUIDFromMap("FinalState", map)
        buttonCaption = map["ButtonCaption"] as? String
    }

}