package com.kvteam.deliverytracker.core.models

import android.arch.persistence.room.Embedded
import android.os.Parcel
import android.os.Parcelable
import java.util.*

data class User(
        var id: UUID? = null,
        var code: String? = null,
        var surname: String? = null,
        var name: String? = null,
        var patronymic: String? = null,
        var phoneNumber: String? = null,
        var role: UUID? = null,
        var instanceId: UUID? = null,
        @Embedded var position: Geoposition? = null) : Parcelable {


    companion object {
        @JvmField
        @Suppress("unused")
        val CREATOR = object : Parcelable.Creator<User> {
            override fun createFromParcel(source: Parcel) = User(source)
            override fun newArray(size: Int): Array<out User?> = arrayOfNulls(size)
        }
    }

    constructor(parcelIn: Parcel) : this(
            parcelIn.readSerializable() as UUID,
            parcelIn.readString(),
            parcelIn.readString(),
            parcelIn.readString(),
            parcelIn.readString(),
            parcelIn.readString(),
            parcelIn.readSerializable() as? UUID?,
            parcelIn.readSerializable() as? UUID?,
            parcelIn.readParcelable(Geoposition::class.java.classLoader)
    )

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeSerializable(id)
        dest.writeString(code)
        dest.writeString(surname)
        dest.writeString(name)
        dest.writeString(patronymic)
        dest.writeString(phoneNumber)
        dest.writeSerializable(role)
        dest.writeSerializable(instanceId)
        dest.writeParcelable(position, flags)
    }

    override fun describeContents() = 0

    open fun fromMap(map: Map<*, *>) {
        val idStr = map["Id"] as? String
        if(idStr != null) {
            try {
                id = UUID.fromString(idStr)
            } catch (e: Exception) {
            }
        }

        val instanceIdStr = map["InstanceIdStr"] as? String
        if(instanceIdStr != null) {
            try {
                instanceId = UUID.fromString(instanceIdStr)
            } catch (e: Exception) {
            }
        }
        code = map["Code"] as? String
        role = map["Role"] as? UUID?
        surname = map["Surname"] as? String
        name = map["Name"] as? String
        patronymic = map["Patronymic"] as? String
        phoneNumber = map["PhoneNumber"] as? String
    }
}