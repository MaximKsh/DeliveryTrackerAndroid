package com.kvteam.deliverytracker.core.models

import com.google.gson.annotations.SerializedName
import com.kvteam.deliverytracker.core.common.MoveAlways
import com.kvteam.deliverytracker.core.common.MoveIfDiffer
import java.io.Serializable

data class Warehouse(
        @SerializedName("Name", alternate = ["name"])
        @MoveIfDiffer
        var name: String? = null,
        @SerializedName("RawAddress", alternate = ["rawAddress"])
        @MoveIfDiffer
        var rawAddress: String? = null,
        @SerializedName("Geoposition", alternate = ["geoposition"])
        @MoveAlways
        var geoposition: Geoposition? = null
): ModelBase(), Serializable {

    override fun fromMap(map: Map<*, *>) {
        super.fromMap(map)
        name = map["Name"] as? String
        rawAddress = map["RawAddress"] as? String
        geoposition = deserializeObjectFromMap("Geoposition", map, {Geoposition()})
    }

}