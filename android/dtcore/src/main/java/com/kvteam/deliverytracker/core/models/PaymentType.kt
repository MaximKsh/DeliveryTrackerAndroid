package com.kvteam.deliverytracker.core.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class PaymentType(@SerializedName("Name", alternate = ["name"])
                  var name: String? = null) : ModelBase(), Serializable {


    override fun fromMap(map: Map<*, *>) {
        super.fromMap(map)
        name = map["Name"] as? String
    }
}