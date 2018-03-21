package com.kvteam.deliverytracker.core.models

import com.google.gson.annotations.SerializedName
import java.util.*

data class TaskStateTransition(
        @SerializedName("Role", alternate = ["role"])
        var role: UUID? = null,
        @SerializedName("InitialState", alternate = ["initialState"])
        var initialState: UUID? = null,
        @SerializedName("FinalState", alternate = ["finalState"])
        var finalState: UUID? = null,
        @SerializedName("ButtonCaption", alternate = ["buttonCaption"])
        var buttonCaption: String? = null
) : ModelBase() {
    override fun fromMap(map: Map<*, *>) {
        super.fromMap(map)
        role = deserializeUUIDFromMap("Role", map)
        initialState = deserializeUUIDFromMap("InitialState", map)
        finalState = deserializeUUIDFromMap("FinalState", map)
        buttonCaption = map["ButtonCaption"] as? String
    }

}