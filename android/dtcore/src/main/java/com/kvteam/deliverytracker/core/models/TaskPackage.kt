package com.kvteam.deliverytracker.core.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class TaskPackage(
        @SerializedName("TaskInfo", alternate = ["taskPackage"])
        var taskInfo: MutableList<TaskInfo> = mutableListOf(),
        @SerializedName("LinkedReferences", alternate = ["linkedReferences"])
        var linkedReferences: MutableMap<String, Map<*, *>> = mutableMapOf(),
        @SerializedName("LinkedUsers", alternate = ["linkedUsers"])
        var linkedUsers: MutableMap<String, User> = mutableMapOf(),
        @SerializedName("TaskProducts", alternate = ["taskProducts"])
        var taskProducts: MutableList<TaskProduct> = mutableListOf()) : Serializable, IMapDeserializable {
    override fun fromMap(map: Map<*, *>) {
        taskInfo = deserializeListObjectsFromMap("TaskInfo", map, {TaskInfo()})
        linkedReferences = deserializeMapFromMap("LinkedReferences", map)
        linkedUsers = deserializeMapObjectsFromMap("LinkedUsers", map, {User()})
        taskProducts = deserializeListObjectsFromMap("TaskProducts", map, {TaskProduct()})
    }
}