package com.kvteam.deliverytracker.core.models

import com.google.gson.annotations.SerializedName
import com.google.gson.internal.LinkedTreeMap
import java.util.*

class Address (id: UUID? = null,
               instanceId: UUID? = null,
               parentId: UUID? = null,
               @SerializedName("RawAddress") var rawAddress: String? = null) :
        ReferenceCollectionBase(id, instanceId,parentId) {
    override fun fromMap(map: Map<*, *>) {
        super.fromMap(map)
        rawAddress = map["RawAddress"] as? String
    }
}