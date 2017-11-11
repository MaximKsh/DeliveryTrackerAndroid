package com.kvteam.deliverytracker.core.models

import android.arch.persistence.room.Embedded
import android.os.Parcel
import android.os.Parcelable

data class UserModel(
        var username: String? = null,
        var surname: String? = null,
        var name: String? = null,
        var phoneNumber: String? = null,
        var role: String? = null,
        @Embedded var instance: InstanceModel? = null,
        @Embedded var position: GeopositionModel? = null) : Parcelable {


    companion object {
        @JvmField
        @Suppress("unused")
        val CREATOR = object : Parcelable.Creator<UserModel> {
            override fun createFromParcel(source: Parcel) = UserModel(source)
            override fun newArray(size: Int): Array<out UserModel?> = arrayOfNulls(size)
        }
    }

    constructor(parcelIn: Parcel) : this(
            parcelIn.readString(),
            parcelIn.readString(),
            parcelIn.readString(),
            parcelIn.readString(),
            parcelIn.readString(),
            parcelIn.readParcelable(InstanceModel::class.java.classLoader),
            parcelIn.readParcelable(GeopositionModel::class.java.classLoader)
    )

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(username)
        dest.writeString(surname)
        dest.writeString(name)
        dest.writeString(phoneNumber)
        dest.writeString(role)
        dest.writeParcelable(instance, flags)
        dest.writeParcelable(position, flags)
    }

    override fun describeContents() = 0
}