package com.kvteam.deliverytracker.core.models

import android.arch.persistence.room.Ignore
import android.os.Parcel
import android.os.Parcelable
import java.util.*


data class DateTimeRangeModel(
        var from: Date? = null,
        var to: Date? = null): Parcelable {

    companion object {
        @JvmField
        @Suppress("unused")
        val CREATOR = object : Parcelable.Creator<DateTimeRangeModel> {
            override fun createFromParcel(source: Parcel) = DateTimeRangeModel(source)
            override fun newArray(size: Int): Array<out DateTimeRangeModel?> = arrayOfNulls(size)
        }
    }

    @Ignore
    constructor(parcelIn: Parcel) : this(
            parcelIn.readSerializable() as Date,
            parcelIn.readSerializable() as Date
    )

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeSerializable(from)
        dest.writeSerializable(to)
    }

    override fun describeContents() = 0
}