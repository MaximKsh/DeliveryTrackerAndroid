package com.kvteam.deliverytracker.core.models

import com.google.gson.internal.LinkedTreeMap
import org.joda.time.DateTime
import org.joda.time.format.ISODateTimeFormat
import java.math.BigDecimal
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

fun deserializeBigDecimalFromMap(key: String, map: Map<*, *>) : BigDecimal? {
    val dbl = map[key] as? Double
    if(dbl != null) {
        return try {
            BigDecimal.valueOf(dbl)
        } catch (e: Exception){
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
        factory: () -> T) : MutableList<T> {
    val serializedList = map[key]
    if(serializedList is List<*>) {
        val typedList = mutableListOf<T>()
        for (objMap in serializedList) {
            if(objMap is Map<*, *>) {
                val obj = factory()
                obj.fromMap(objMap)
                typedList.add(obj)
            }
        }
        return typedList
    }
    return mutableListOf()
}

fun deserializeListMapFromMap(
        key: String,
        map: Map<*, *>) : MutableList<MutableMap<*, *>> {
    val serializedList = map[key]
    if(serializedList is List<*>) {
        return serializedList
                .filterIsInstance<MutableMap<*, *>>()
                .toMutableList()
    }
    return mutableListOf()
}

fun deserializeMapFromMap(
        key: String,
        map: Map<*, *>) : MutableMap<String, Map<*, *>> {
    val serializedMap = map[key]
    if(serializedMap is Map<*, *>) {
        val typedMap = mutableMapOf<String, Map<*, *>>()
        for (pair in serializedMap.entries) {
            val pairKey = pair.key
            val pairValue = pair.value
            if(pairKey is String
                    && pairValue is Map<*, *>) {
                typedMap[pairKey] = pairValue
            }
        }
        return typedMap
    }
    return mutableMapOf()
}

fun deserializeAnyMapFromMap(
        key: String,
        map: Map<*, *>) : MutableMap<String, Any?> {
    val serializedMap = map[key]
    if(serializedMap is Map<*, *>) {
        val typedMap = mutableMapOf<String, Any?>()
        for (pair in serializedMap.entries) {
            val pairKey = pair.key
            val pairValue = pair.value
            if(pairKey is String) {
                typedMap[pairKey] = pairValue
            }
        }
        return typedMap
    }
    return mutableMapOf()
}

fun <T : IMapDeserializable> deserializeMapObjectsFromMap(
        key: String,
        map: Map<*, *>,
        factory: () -> T) : MutableMap<String, T> {
    val serializedMap = map[key]
    if(serializedMap is Map<*, *>) {
        val typedMap = mutableMapOf<String, T>()
        for (pair in serializedMap.entries) {
            val pairKey = pair.key
            val pairValue = pair.value
            if(pairKey is String
                && pairValue is Map<*, *>) {
                val obj = factory()
                obj.fromMap(pairValue)
                typedMap[pairKey] = obj
            }
        }
        return typedMap
    }
    return mutableMapOf()
}

fun convertToInt (obj: Any?): Int? {
    if (obj == null) {
        return null
    }
    if (obj is Double) {
        return obj.toInt()
    }
    return try {
        obj.toString().toInt()
    } catch(e1: NumberFormatException) {
        null
    } catch (e2: IllegalArgumentException) {
        null
    }
}