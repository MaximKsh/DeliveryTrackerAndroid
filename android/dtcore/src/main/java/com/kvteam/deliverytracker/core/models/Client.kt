package com.kvteam.deliverytracker.core.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class Client(
    @SerializedName("Surname", alternate = ["surname"])
    var surname: String? = null,
    @SerializedName("Name", alternate = ["name"])
    var name: String? = null,
    @SerializedName("Patronymic", alternate = ["patronymic"])
    var patronymic: String? = null,
    @SerializedName("PhoneNumber", alternate = ["phoneNumber"])
    var phoneNumber: String? = null,
    @SerializedName("Addresses", alternate = ["addresses"])
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