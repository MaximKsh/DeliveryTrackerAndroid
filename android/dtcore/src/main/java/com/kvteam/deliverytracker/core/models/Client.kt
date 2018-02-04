package com.kvteam.deliverytracker.core.models

import com.google.gson.annotations.SerializedName
import com.google.gson.internal.LinkedTreeMap
import java.util.*

class Client(id: UUID? = null,
             instanceId: UUID? = null,
             @SerializedName("Surname") var surname: String? = null,
             @SerializedName("Name") var name: String? = null,
             @SerializedName("Patronymic") var patronymic: String? = null,
             @SerializedName("PhoneNumber") var phoneNumber: String? = null,
             @SerializedName("Addresses") var addresses: MutableList<Address> = mutableListOf()) :
        ReferenceEntityBase(id, instanceId) {
    override fun fromMap(map: Map<*, *>) {
        super.fromMap(map)
        surname = map["Surname"] as? String
        name = map["Name"] as? String
        patronymic = map["Patronymic"] as? String
        phoneNumber = map["PhoneNumber"] as? String

        val addressesSerialized = map["Addresses"]
        if(addressesSerialized is List<*>) {
            addresses = mutableListOf()
            for (addr in addressesSerialized) {
                if(addr is LinkedTreeMap<*, *>) {
                    val address = Address()
                    address.fromMap(addr)
                    addresses.add(address)
                }
            }
        }
    }
}