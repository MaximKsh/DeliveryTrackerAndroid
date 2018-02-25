package com.kvteam.deliverytracker.core.models

import com.google.gson.annotations.SerializedName

class ClientAddress : CollectionModelBase() {

    @SerializedName("RawAddress")
    var rawAddress: String? = null
    @SerializedName("Geoposition")
    var geoposition: Geoposition? = null

    override fun fromMap(map: Map<*, *>) {
        super.fromMap(map)
        rawAddress = map["RawAddress"] as? String
        geoposition = deserializeObjectFromMap("Geoposition", map, { Geoposition() })
    }
}