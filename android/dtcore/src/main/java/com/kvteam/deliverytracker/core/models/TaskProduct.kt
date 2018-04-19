package com.kvteam.deliverytracker.core.models

import com.google.gson.annotations.SerializedName
import java.util.*

data class TaskProduct(
        @SerializedName("ProductId", alternate = ["productId"])
        var productId: UUID? = null,
        @SerializedName("Quantity", alternate = ["quantity"])
        var quantity: Int? = null) : CollectionModelBase(), IMapDeserializable {
    override fun fromMap(map: Map<*, *>) {
        super.fromMap(map)
        productId = deserializeUUIDFromMap("ProductId", map)
        quantity = map["Quantity"] as? Int
    }

}