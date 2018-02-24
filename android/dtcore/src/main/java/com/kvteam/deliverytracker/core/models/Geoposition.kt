package com.kvteam.deliverytracker.core.models

import android.arch.persistence.room.Ignore
import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class Geoposition(
        @SerializedName("Longitude") var longitude: Double = 0.0,
        @SerializedName("Latitude") var latitude: Double = 0.0) : Parcelable {
    companion object {
        @JvmField
        @Suppress("unused")
        val CREATOR = object : Parcelable.Creator<Geoposition> {
            override fun createFromParcel(source: Parcel) = Geoposition(source)
            override fun newArray(size: Int): Array<out Geoposition?> = arrayOfNulls(size)
        }
    }

    @Ignore
    constructor(parcelIn: Parcel) : this(
            parcelIn.readDouble(),
            parcelIn.readDouble()
    )

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeDouble(longitude)
        dest.writeDouble(latitude)
    }

    override fun describeContents() = 0

    fun fromMap(map: Map<*, *>) {
        val lon = map["Longitude"] as? Double
        if(lon != null) {
            longitude = lon
        }
        val lat = map["Latitude"] as? Double
        if(lat != null) {
            latitude = lat
        }
    }
}