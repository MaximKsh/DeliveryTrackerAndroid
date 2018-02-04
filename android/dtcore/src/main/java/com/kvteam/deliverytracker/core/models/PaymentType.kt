package com.kvteam.deliverytracker.core.models

import com.google.gson.annotations.SerializedName
import com.google.gson.internal.LinkedTreeMap
import java.util.*

class PaymentType(
         id: UUID? = null,
         instanceId: UUID? = null,
         @SerializedName("Name") var name: String? = null) : ReferenceEntityBase(id, instanceId) {
    override fun fromMap(map: Map<*, *>) {
        super.fromMap(map)
        name = map["Name"] as? String
    }
}