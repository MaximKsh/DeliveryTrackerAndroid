package com.kvteam.deliverytracker.core.models

import com.google.gson.annotations.SerializedName
import com.google.gson.internal.LinkedTreeMap
import java.math.BigDecimal
import java.util.*

class Product(id: UUID? = null,
              instanceId: UUID? = null,
              @SerializedName("VendorCode") var vendorCode: String? = null,
              @SerializedName("Name") var name: String? = null,
              @SerializedName("Description") var description: String? = null,
              @SerializedName("Cost") var cost: BigDecimal? = null) : ReferenceEntityBase(id, instanceId) {
    override fun fromMap(map: Map<*, *>) {
        super.fromMap(map)
        vendorCode = map["VendorCode"] as? String
        name = map["Name"] as? String
        description = map["Description"] as? String
        cost = map["Cost"] as? BigDecimal
    }
}