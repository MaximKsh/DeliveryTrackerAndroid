package com.kvteam.deliverytracker.core.models

import android.os.Parcel
import android.os.Parcelable

data class InstanceModel(var instanceName: String? = null): Parcelable {

    companion object {
        @JvmField
        @Suppress("unused")
        val CREATOR = object : Parcelable.Creator<InstanceModel> {
            override fun createFromParcel(source: Parcel) = InstanceModel(source)
            override fun newArray(size: Int): Array<out InstanceModel?> = arrayOfNulls(size)
        }
    }

    constructor(parcelIn: Parcel) : this(
            parcelIn.readString()
    )

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(instanceName)
    }

    override fun describeContents() = 0
}