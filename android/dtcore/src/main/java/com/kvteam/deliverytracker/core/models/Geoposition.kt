package com.kvteam.deliverytracker.core.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Geoposition (
        @SerializedName("Longitude", alternate = ["longiture"])
        var longitude: Double = 0.0,
        @SerializedName("Latitude", alternate = ["latitude"])
        var latitude: Double = 0.0
) : Serializable, IMapDeserializable {

    override fun fromMap(map: Map<*, *>) {
        longitude = map["Longitude"] as? Double ?: 0.0
        latitude = map["Latitude"] as? Double ?: 0.0
    }
}