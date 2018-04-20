package com.kvteam.deliverytracker.core.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ResponseReferencePackage(
        @SerializedName("Entry", alternate = ["entry"])
        var entry: MutableMap<*, *> = mutableMapOf<String, Any>(),
        @SerializedName("Collections", alternate = ["collections"])
        var collections: MutableList<MutableMap<*, *>> = mutableListOf()
) : Serializable, IMapDeserializable {
    override fun fromMap(map: Map<*, *>) {
        entry = deserializeAnyMapFromMap("Entry", map)
        collections = deserializeListMapFromMap("Collections", map)
    }
}