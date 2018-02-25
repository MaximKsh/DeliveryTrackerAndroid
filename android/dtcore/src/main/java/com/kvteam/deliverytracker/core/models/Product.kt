package com.kvteam.deliverytracker.core.models

import com.google.gson.annotations.SerializedName
import java.math.BigDecimal

class Product : ModelBase() {
    @SerializedName("VendorCode")
    var vendorCode: String? = null
    @SerializedName("Name")
    var name: String? = null
    @SerializedName("Description")
    var description: String? = null
    @SerializedName("Cost")
    var cost: BigDecimal? = null

    override fun fromMap(map: Map<*, *>) {
        super.fromMap(map)
        vendorCode = map["VendorCode"] as? String
        name = map["Name"] as? String
        description = map["Description"] as? String
        cost = map["Cost"] as? BigDecimal
    }

}