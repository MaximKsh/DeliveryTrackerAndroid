package com.kvteam.deliverytracker.core.models

import com.google.gson.annotations.SerializedName
import com.kvteam.deliverytracker.core.common.MoveAlways
import com.kvteam.deliverytracker.core.common.MoveIfDiffer
import java.io.Serializable


data class Client(
    @SerializedName("Surname", alternate = ["surname"])
    @MoveIfDiffer
    var surname: String? = null,
    @SerializedName("Name", alternate = ["name"])
    @MoveIfDiffer
    var name: String? = null,
    @SerializedName("Patronymic", alternate = ["patronymic"])
    @MoveIfDiffer
    var patronymic: String? = null,
    @SerializedName("PhoneNumber", alternate = ["phoneNumber"])
    @MoveIfDiffer
    var phoneNumber: String? = null,
    @SerializedName("Addresses", alternate = ["addresses"])
    @MoveAlways
    var clientAddresses: MutableList<ClientAddress> = mutableListOf()
): ModelBase(), Serializable {

    override fun fromMap(map: Map<*, *>) {
        super.fromMap(map)
        surname = map["Surname"] as? String
        name = map["Name"] as? String
        patronymic = map["Patronymic"] as? String
        phoneNumber = map["PhoneNumber"] as? String
        clientAddresses = deserializeListObjectsFromMap("Addresses", map, { ClientAddress() })

    }
}