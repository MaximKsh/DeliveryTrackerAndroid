package com.kvteam.deliverytracker.core.models

import com.google.android.gms.maps.model.LatLng
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Geoposition (
        @SerializedName("Longitude", alternate = ["longiture"])
        var longitude: Double = 0.0,
        @SerializedName("Latitude", alternate = ["latitude"])
        var latitude: Double = 0.0
) : Serializable, IMapDeserializable {

    fun toLtnLng(): LatLng {
        return LatLng(latitude, longitude)
    }

    fun toDirectionsLtnLng(): com.google.maps.model.LatLng {
        return com.google.maps.model.LatLng(latitude, longitude)
    }

    override fun fromMap(map: Map<*, *>) {
        longitude = map["Longitude"] as? Double ?: 0.0
        latitude = map["Latitude"] as? Double ?: 0.0
    }
}
