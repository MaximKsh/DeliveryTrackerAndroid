package com.kvteam.deliverytracker.core.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.math.BigDecimal

class Product(
        @SerializedName("VendorCode", alternate = ["vendorCode"])
        var vendorCode: String? = null,
        @SerializedName("Name", alternate = ["name"])
        var name: String? = null,
        @SerializedName("Description", alternate = ["description"])
        var description: String? = null,
        @SerializedName("Cost", alternate = ["cost"])
        var cost: BigDecimal? = null
) : ModelBase(), Serializable {
    override fun fromMap(map: Map<*, *>) {
        super.fromMap(map)
        vendorCode = map["VendorCode"] as? String
        name = map["Name"] as? String
        description = map["Description"] as? String
        cost = deserializeBigDecimalFromMap("Cost", map)
    }

}