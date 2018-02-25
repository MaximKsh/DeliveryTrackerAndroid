package com.kvteam.deliverytracker.core.models

import com.google.gson.annotations.SerializedName

class Client : ModelBase() {
    @SerializedName("Surname")
    var surname: String? = null
    @SerializedName("Name")
    var name: String? = null
    @SerializedName("Patronymic")
    var patronymic: String? = null
    @SerializedName("PhoneNumber")
    var phoneNumber: String? = null
    @SerializedName("Addresses")
    var clientAddresses: MutableList<ClientAddress> = mutableListOf()

    override fun fromMap(map: Map<*, *>) {
        super.fromMap(map)
        surname = map["Surname"] as? String
        name = map["Name"] as? String
        patronymic = map["Patronymic"] as? String
        phoneNumber = map["PhoneNumber"] as? String
        val addresses = deserializeListObjectsFromMap("Addresses", map, { ClientAddress() })
        if(addresses != null) {
            clientAddresses = addresses.toMutableList()
        }
    }
}