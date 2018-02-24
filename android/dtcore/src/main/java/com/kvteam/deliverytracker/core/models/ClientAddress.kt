package com.kvteam.deliverytracker.core.models

import com.google.gson.annotations.SerializedName
import com.google.gson.internal.LinkedTreeMap
import java.util.*

class ClientAddress(id: UUID? = null,
                    instanceId: UUID? = null,
                    parentId: UUID? = null,
                    @SerializedName("RawAddress") var rawAddress: String? = null,
                    @SerializedName("Geoposition") var geoposition: Geoposition? = null) :
        ReferenceCollectionBase(id, instanceId,parentId) {
    override fun fromMap(map: Map<*, *>) {
        super.fromMap(map)
        rawAddress = map["RawAddress"] as? String
        val geoObj = map["Geoposition"]
        if(geoObj is LinkedTreeMap<*, *>) {
            geoposition = Geoposition()
            geoposition?.fromMap(geoObj)
        }
    }
}