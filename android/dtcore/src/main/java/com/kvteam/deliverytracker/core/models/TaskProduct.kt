package com.kvteam.deliverytracker.core.models

import com.google.gson.annotations.SerializedName
import java.util.*

class TaskProduct : IMapDeserializable {
    @SerializedName("ProductId", alternate = ["productId"])
    var productId: UUID? = null

    @SerializedName("Quantity", alternate = ["quantity"])
    var quantity: Int? = null

    override fun fromMap(map: Map<*, *>) {
        productId = deserializeUUIDFromMap("ProductId", map)
        quantity = map["Quantity"] as? Int
    }

}