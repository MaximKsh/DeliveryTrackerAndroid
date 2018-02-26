package com.kvteam.deliverytracker.core.models

import com.google.gson.annotations.SerializedName

class PaymentType : ModelBase() {
    @SerializedName("Name", alternate = ["name"])
    var name: String? = null

    override fun fromMap(map: Map<*, *>) {
        super.fromMap(map)
        name = map["Name"] as? String
    }
}