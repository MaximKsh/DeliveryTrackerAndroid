package com.kvteam.deliverytracker.core.models

import android.arch.persistence.room.Ignore
import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

class Geoposition () : Parcelable, IMapDeserializable {

    @SerializedName("Longitude", alternate = ["longiture"])
    var longitude: Double = 0.0
    @SerializedName("Latitude", alternate = ["latitude"])
    var latitude: Double = 0.0

    companion object {
        @JvmField
        @Suppress("unused")
        val CREATOR = object : Parcelable.Creator<Geoposition> {
            override fun createFromParcel(source: Parcel) = Geoposition(source)
            override fun newArray(size: Int): Array<out Geoposition?> = arrayOfNulls(size)
        }
    }

    @Ignore
    constructor(parcelIn: Parcel) : this()
    {
        longitude = parcelIn.readDouble()
        latitude = parcelIn.readDouble()
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeDouble(longitude)
        dest.writeDouble(latitude)
    }

    override fun describeContents() = 0

    override fun fromMap(map: Map<*, *>) {
        longitude = map["Longitude"] as? Double ?: 0.0
        latitude = map["Latitude"] as? Double ?: 0.0
    }
}