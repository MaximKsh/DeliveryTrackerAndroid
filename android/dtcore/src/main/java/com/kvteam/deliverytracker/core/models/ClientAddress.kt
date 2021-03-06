package com.kvteam.deliverytracker.core.models

import com.google.gson.annotations.SerializedName
import com.kvteam.deliverytracker.core.common.MoveIfDiffer
import java.io.Serializable

data class ClientAddress(
        @SerializedName("RawAddress", alternate = ["rawAddress"])
        @MoveIfDiffer
        override var rawAddress: String? = null,
        @SerializedName("Geoposition", alternate = ["geoposition"])
        @MoveIfDiffer
        override var geoposition: Geoposition? = null
) : CollectionModelBase(), Serializable, IAddress {
    override fun fromMap(map: Map<*, *>) {
        super.fromMap(map)
        rawAddress = map["RawAddress"] as? String
        geoposition = deserializeObjectFromMap("Geoposition", map, { Geoposition() })
    }
}