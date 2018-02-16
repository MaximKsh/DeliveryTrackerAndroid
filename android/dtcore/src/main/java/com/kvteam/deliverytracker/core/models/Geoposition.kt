package com.kvteam.deliverytracker.core.models

import android.arch.persistence.room.Ignore
import android.os.Parcel
import android.os.Parcelable

data class Geoposition(
        var longitude: Double = 0.0,
        var latitude: Double = 0.0) : Parcelable {
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
}