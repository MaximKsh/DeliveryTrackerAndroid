package com.kvteam.deliverytracker.core.models

import com.google.gson.annotations.SerializedName

data class RequestReferencePackage(
        @SerializedName("Entry", alternate = ["entry"])
        var entry: ModelBase,
        @SerializedName("Collections", alternate = ["collections"])
        var collections: MutableList<CollectionModelBase> = mutableListOf()
)