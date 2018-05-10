package com.kvteam.deliverytracker.core.models

import com.google.gson.annotations.SerializedName
import com.kvteam.deliverytracker.core.common.MoveAlways
import com.kvteam.deliverytracker.core.common.MoveIfDiffer
import java.util.*

data class TaskProduct(
        @SerializedName("ProductId", alternate = ["productId"])
        @MoveAlways
        var productId: UUID? = null,
        @SerializedName("Quantity", alternate = ["quantity"])
        @MoveIfDiffer
        var quantity: Int? = null) : CollectionModelBase(), IMapDeserializable {
    override fun fromMap(map: Map<*, *>) {
        super.fromMap(map)
        productId = deserializeUUIDFromMap("ProductId", map)
        quantity = map["Quantity"] as? Int
    }

}