package com.kvteam.deliverytracker.core.common

import android.os.Parcel
import android.os.Parcelable

data class ErrorItem(val message: String) : Parcelable {
    companion object {
        @JvmField
        @Suppress("unused")
        val CREATOR = object : Parcelable.Creator<ErrorItem> {
            override fun createFromParcel(source: Parcel) = ErrorItem(source)
            override fun newArray(size: Int): Array<out ErrorItem?> = arrayOfNulls(size)
        }
    }

    constructor(parcelIn: Parcel) : this(parcelIn.readString())

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(message)
    }

    override fun describeContents() = 0
}