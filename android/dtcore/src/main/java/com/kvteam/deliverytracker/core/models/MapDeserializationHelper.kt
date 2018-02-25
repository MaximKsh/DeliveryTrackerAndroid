package com.kvteam.deliverytracker.core.models

import com.google.gson.internal.LinkedTreeMap
import org.joda.time.DateTime
import org.joda.time.format.ISODateTimeFormat
import java.util.*

fun deserializeUUIDFromMap(key: String, map: Map<*, *>) : UUID? {
    val idStr = map[key] as? String
    if(idStr != null) {
        return try {
            UUID.fromString(idStr)
        } catch (e: Exception) {
            null
        }
    }
    return null
}

fun deserializeDateTimeFromMap(key: String, map: Map<*, *>) : DateTime? {
    val dateStr = map[key] as? String
    if(dateStr != null) {
        return try {
            ISODateTimeFormat.dateTimeParser().parseDateTime(dateStr)
        } catch (e: Exception) {
            null
        }
    }
    return null
}

fun <T : IMapDeserializable> deserializeObjectFromMap(
        key: String,
        map: Map<*, *>,
        factory: () -> T) : T? {
    val serialized = map[key]
    if(serialized is LinkedTreeMap<*, *>) {
        val obj = factory()
        obj.fromMap(serialized)
        return obj
    }
    return null
}

fun <T : IMapDeserializable> deserializeListObjectsFromMap(
        key: String,
        map: Map<*, *>,
        factory: () -> T) : List<T>? {
    val serializedList = map[key]
    if(serializedList is List<*>) {
        val typedList = mutableListOf<T>()
        for (objMap in serializedList) {
            if(objMap is LinkedTreeMap<*, *>) {
                val obj = factory()
                obj.fromMap(objMap)
                typedList.add(obj)
            }
        }
        return typedList
    }
    return null
}