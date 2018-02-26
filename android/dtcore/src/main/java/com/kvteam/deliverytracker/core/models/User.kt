package com.kvteam.deliverytracker.core.models

import android.arch.persistence.room.Embedded
import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import java.util.*

class User() : ModelBase(), Parcelable {

    @SerializedName("Code", alternate = ["code"])
    var code: String? = null

    @SerializedName("Surname", alternate = ["surname"])
    var surname: String? = null

    @SerializedName("Name", alternate = ["name"])
    var name: String? = null

    @SerializedName("Patronymic", alternate = ["patronymic"])
    var patronymic: String? = null

    @SerializedName("PhoneNumber", alternate = ["phoneNumber"])
    var phoneNumber: String? = null

    @SerializedName("Role", alternate = ["role"])
    var role: UUID? = null

    @SerializedName("Geoposition", alternate = ["geoposition"])
    @Embedded
    var geoposition: Geoposition? = null

    override fun fromMap(map: Map<*, *>) {
        super.fromMap(map)
        code = map["Code"] as? String
        role = deserializeUUIDFromMap("Role", map)
        surname = map["Surname"] as? String
        name = map["Name"] as? String
        patronymic = map["Patronymic"] as? String
        phoneNumber = map["PhoneNumber"] as? String
        geoposition = deserializeObjectFromMap("Geoposition", map, {Geoposition()})
    }

    companion object {
        @JvmField
        @Suppress("unused")
        val CREATOR = object : Parcelable.Creator<User> {
            override fun createFromParcel(source: Parcel) = User(source)
            override fun newArray(size: Int): Array<out User?> = arrayOfNulls(size)
        }
    }

    constructor(parcelIn: Parcel) : this() {
        id = parcelIn.readSerializable() as UUID
        code = parcelIn.readString()
        surname = parcelIn.readString()
        name = parcelIn.readString()
        patronymic = parcelIn.readString()
        phoneNumber = parcelIn.readString()
        role = parcelIn.readSerializable() as? UUID?
        instanceId = parcelIn.readSerializable() as? UUID?
        geoposition = parcelIn.readParcelable(Geoposition::class.java.classLoader)
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeSerializable(id)
        dest.writeString(code)
        dest.writeString(surname)
        dest.writeString(name)
        dest.writeString(patronymic)
        dest.writeString(phoneNumber)
        dest.writeSerializable(role)
        dest.writeSerializable(instanceId)
        dest.writeParcelable(geoposition, flags)
    }

    override fun describeContents() = 0

}