package com.kvteam.deliverytracker.core.models

import com.google.gson.annotations.SerializedName

class Warehouse: ModelBase() {
    @SerializedName("Name", alternate = ["name"])
    var name: String? = null
    @SerializedName("RawAddress", alternate = ["rawAddress"])
    var rawAddress: String? = null
    @SerializedName("Geoposition", alternate = ["geoposition"])
    var geoposition: Geoposition? = null

    override fun fromMap(map: Map<*, *>) {
        super.fromMap(map)
        rawAddress = map["RawAddress"] as? String
        name = map["Name"] as? String
        geoposition = deserializeObjectFromMap("Geoposition", map, {Geoposition()})
    }

}