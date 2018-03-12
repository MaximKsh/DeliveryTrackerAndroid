package com.kvteam.deliverytracker.core.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Warehouse(
        @SerializedName("Name", alternate = ["name"])
        var name: String? = null,
        @SerializedName("RawAddress", alternate = ["rawAddress"])
        var rawAddress: String? = null,
        @SerializedName("Geoposition", alternate = ["geoposition"])
        var geoposition: Geoposition? = null
): ModelBase(), Serializable {

    override fun fromMap(map: Map<*, *>) {
        super.fromMap(map)
        name = map["Name"] as? String
        rawAddress = map["RawAddress"] as? String
        geoposition = deserializeObjectFromMap("Geoposition", map, {Geoposition()})
    }

}