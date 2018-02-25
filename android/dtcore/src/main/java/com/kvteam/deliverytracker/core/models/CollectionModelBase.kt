package com.kvteam.deliverytracker.core.models

import com.google.gson.annotations.SerializedName
import java.util.*

open class CollectionModelBase : ModelBase() {
    @SerializedName("ParentId")
    var parentId: UUID? = null
    @SerializedName("Action")
    var action: CollectionEntityAction = CollectionEntityAction.None

    override fun fromMap(map: Map<*, *>) {
        super.fromMap(map)
        parentId = deserializeUUIDFromMap("ParentId", map)
    }
}