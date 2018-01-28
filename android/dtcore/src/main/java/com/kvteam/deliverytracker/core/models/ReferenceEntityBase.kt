package com.kvteam.deliverytracker.core.models

import com.google.gson.annotations.SerializedName
import com.google.gson.internal.LinkedTreeMap
import java.util.*

open class ReferenceEntityBase(@SerializedName("Id") var id: UUID? = null,
                               @SerializedName("InstanceId") var instanceId: UUID? = null) {

    open fun fromMap(map: Map<*, *>) {
        val idStr = map["Id"] as? String
        if(idStr != null) {
            try {
                id = UUID.fromString(idStr)
            } catch (e: Exception) {
            }
        }
        val instanceIdStr = map["InstanceId"] as? String
        if(instanceIdStr != null) {
            try {
                instanceId = UUID.fromString(instanceIdStr)
            } catch (e: Exception) {
            }
        }
    }

}