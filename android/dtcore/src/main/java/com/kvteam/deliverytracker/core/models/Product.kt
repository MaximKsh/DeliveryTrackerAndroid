package com.kvteam.deliverytracker.core.models

import com.google.gson.annotations.SerializedName
import com.kvteam.deliverytracker.core.common.MoveIfDiffer
import java.io.Serializable
import java.math.BigDecimal

class Product(
        @SerializedName("VendorCode", alternate = ["vendorCode"])
        @MoveIfDiffer
        var vendorCode: String? = null,
        @SerializedName("Name", alternate = ["name"])
        @MoveIfDiffer
        var name: String? = null,
        @SerializedName("Description", alternate = ["description"])
        @MoveIfDiffer
        var description: String? = null,
        @SerializedName("Cost", alternate = ["cost"])
        @MoveIfDiffer
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