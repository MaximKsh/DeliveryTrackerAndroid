package com.kvteam.deliverytracker.core.models

import com.google.gson.annotations.SerializedName
import java.util.*

open class ReferenceCollectionBase(
        id: UUID? = null,
        instanceId: UUID? = null,
        @SerializedName("ParentId") var parentId: UUID? = null,
        @SerializedName("Action") var action: CollectionEntityAction = CollectionEntityAction.None) :
            ReferenceEntityBase(id, instanceId) {
    override fun fromMap(map: Map<*, *>) {
        super.fromMap(map)
        val parentIdStr = map["ParentId"] as? String
        if(parentIdStr != null) {
            try {
                parentId = UUID.fromString(parentIdStr)
            } catch (e: Exception) {
            }
        }

    }
}