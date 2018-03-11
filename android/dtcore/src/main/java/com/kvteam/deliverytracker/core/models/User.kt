package com.kvteam.deliverytracker.core.models

import android.arch.persistence.room.Embedded
import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.util.*

data class User(
    @SerializedName("Code", alternate = ["code"])
    var code: String? = null,
    @SerializedName("Surname", alternate = ["surname"])
    var surname: String? = null,
    @SerializedName("Name", alternate = ["name"])
    var name: String? = null,
    @SerializedName("Patronymic", alternate = ["patronymic"])
    var patronymic: String? = null,
    @SerializedName("PhoneNumber", alternate = ["phoneNumber"])
    var phoneNumber: String? = null,
    @SerializedName("Role", alternate = ["role"])
    var role: UUID? = null,
    @SerializedName("Geoposition", alternate = ["geoposition"])
    @Embedded
    var geoposition: Geoposition? = null,
    @SerializedName("Online", alternate = ["online"])
    var online: Boolean = false
): ModelBase(), Serializable {

    override fun fromMap(map: Map<*, *>) {
        super.fromMap(map)
        code = map["Code"] as? String
        role = deserializeUUIDFromMap("Role", map)
        surname = map["Surname"] as? String
        name = map["Name"] as? String
        patronymic = map["Patronymic"] as? String
        phoneNumber = map["PhoneNumber"] as? String
        geoposition = deserializeObjectFromMap("Geoposition", map, {Geoposition()})
        online = map["Online"] as? Boolean ?: false
    }

}