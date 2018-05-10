package com.kvteam.deliverytracker.core.models

import com.google.gson.annotations.SerializedName
import org.joda.time.DateTime

data class TaskStatisticsItem (
    @SerializedName("Key", alternate = ["key"])
    var key: String? = null,
    @SerializedName("DatePoint", alternate = ["datePoint"])
    var datePoint: DateTime? = null,
    @SerializedName("Created", alternate = ["created"])
    var created: Int? = null,
    @SerializedName("Completed", alternate = ["completed"])
    var completed: Int? = null,
    @SerializedName("Preparing", alternate = ["preparing"])
    var preparing: Int? = null,
    @SerializedName("Queue", alternate = ["queue"])
    var queue: Int? = null,
    @SerializedName("Waiting", alternate = ["waiting"])
    var waiting: Int? = null,
    @SerializedName("IntoWork", alternate = ["intoWork"])
    var intoWork: Int? = null,
    @SerializedName("Delivered", alternate = ["delivered"])
    var delivered: Int? = null,
    @SerializedName("Complete", alternate = ["complete"])
    var complete: Int? = null,
    @SerializedName("Revoked", alternate = ["revoked"])
    var revoked: Int? = null
) : IMapDeserializable {
    override fun fromMap(map: Map<*, *>) {
        key = map["Key"] as? String
        datePoint = deserializeDateTimeFromMap("DatePoint", map)
        created = convertToInt(map["Created"])
        completed = convertToInt(map["Completed"])
        preparing = convertToInt(map["Preparing"])
        queue = convertToInt(map["Queue"])
        waiting = convertToInt(map["Waiting"])
        intoWork = convertToInt(map["IntoWork"])
        delivered = convertToInt(map["Delivered"])
        complete = convertToInt(map["Complete"])
        revoked = convertToInt(map["Revoked"])

    }
}