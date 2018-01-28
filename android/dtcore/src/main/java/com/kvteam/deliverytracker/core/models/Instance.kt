package com.kvteam.deliverytracker.core.models

import android.arch.persistence.room.Ignore
import android.os.Parcel
import android.os.Parcelable
import java.util.*

data class Instance(
        var id: UUID? = null,
        var name: String? = null,
        var creatorId: UUID? = null): Parcelable {

    companion object {
        @JvmField
        @Suppress("unused")
        val CREATOR = object : Parcelable.Creator<Instance> {
            override fun createFromParcel(source: Parcel) = Instance(source)
            override fun newArray(size: Int): Array<out Instance?> = arrayOfNulls(size)
        }
    }

    @Ignore
    constructor(parcelIn: Parcel) : this(
            parcelIn.readSerializable() as UUID,
            parcelIn.readString(),
            parcelIn.readSerializable() as UUID
    )

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeSerializable(id)
        dest.writeString(name)
        dest.writeSerializable(creatorId)
    }

    override fun describeContents() = 0
}