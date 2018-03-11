package com.kvteam.deliverytracker.core.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ClientAddress(
        @SerializedName("RawAddress", alternate = ["rawAddress"])
        var rawAddress: String? = null,
        @SerializedName("Geoposition", alternate = ["geoposition"])
        var geoposition: Geoposition? = null
) : CollectionModelBase(), Serializable {
    override fun fromMap(map: Map<*, *>) {
        super.fromMap(map)
        rawAddress = map["RawAddress"] as? String
        geoposition = deserializeObjectFromMap("Geoposition", map, { Geoposition() })
    }
}