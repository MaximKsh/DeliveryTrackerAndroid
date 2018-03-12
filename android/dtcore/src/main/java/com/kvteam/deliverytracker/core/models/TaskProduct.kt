package com.kvteam.deliverytracker.core.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.util.*

data class TaskProduct(
        @SerializedName("ProductId", alternate = ["productId"])
        var productId: UUID? = null,
        @SerializedName("Quantity", alternate = ["quantity"])
        var quantity: Int? = null) : Serializable, IMapDeserializable {
    override fun fromMap(map: Map<*, *>) {
        productId = deserializeUUIDFromMap("ProductId", map)
        quantity = map["Quantity"] as? Int
    }

}