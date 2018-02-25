package com.kvteam.deliverytracker.core.models

interface IMapDeserializable {
    fun fromMap(map: Map<*, *>)
}