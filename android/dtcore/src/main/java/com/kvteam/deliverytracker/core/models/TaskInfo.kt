package com.kvteam.deliverytracker.core.models

import com.google.gson.annotations.SerializedName
import org.joda.time.DateTime
import java.io.Serializable
import java.math.BigDecimal
import java.util.*

data class TaskInfo(
    @SerializedName("TaskStateId", alternate = ["taskStateId"])
    var taskStateId: UUID? = null,
    @SerializedName("TaskStateName", alternate = ["taskStateName"])
    var taskStateName: String? = null,
    @SerializedName("TaskStateCaption", alternate = ["taskStateCaption"])
    var taskStateCaption: String? = null,
    @SerializedName("AuthorId", alternate = ["authorId"])
    var authorId: UUID? = null,
    @SerializedName("PerformerId", alternate = ["performerId"])
    var performerId: UUID? = null,
    @SerializedName("TaskNumber", alternate = ["taskNumber"])
    var taskNumber: String? = null,
    @SerializedName("Created", alternate = ["created"])
    var created: DateTime? = null,
    @SerializedName("StateChangedLastTime", alternate = ["stateChangedLastTime"])
    var stateChangedLastTime: DateTime? = null,
    @SerializedName("Receipt", alternate = ["receipt"])
    var receipt: DateTime? = null,
    @SerializedName("ReceiptActual", alternate = ["receiptActual"])
    var receiptActual: DateTime? = null,
    @SerializedName("DeliveryFrom", alternate = ["deliveryFrom"])
    var deliveryFrom: DateTime? = null,
    @SerializedName("DeliveryTo", alternate = ["deliveryTo"])
    var deliveryTo: DateTime? = null,
    @SerializedName("DeliveryActual", alternate = ["deliveryActual"])
    var deliveryActual: DateTime? = null,
    @SerializedName("Comment", alternate = ["comment"])
    var comment: String? = null,
    @SerializedName("WarehouseId", alternate = ["warehouseId"])
    var warehouseId: UUID? = null,
    @SerializedName("PaymentTypeId", alternate = ["paymentTypeId"])
    var paymentTypeId: UUID? = null,
    @SerializedName("ClientId", alternate = ["clientId"])
    var clientId: UUID? = null,
    @SerializedName("ClientAddressId", alternate = ["clientAddressId"])
    var clientAddressId: UUID? = null,
    @SerializedName("Cost", alternate = ["cost"])
    var cost: BigDecimal? = null,
    @SerializedName("DeliveryCost", alternate = ["deliveryCost"])
    var deliveryCost: BigDecimal? = null,
    @SerializedName("TaskProducts", alternate = ["taskProducts"])
    var taskProducts: MutableList<TaskProduct> = mutableListOf()
) : ModelBase(), Serializable {

    override fun fromMap(map: Map<*, *>) {
        super.fromMap(map)
        taskStateId = deserializeUUIDFromMap("TaskStateId", map)
        taskStateName = map["TaskStateName"] as? String
        taskStateCaption = map["TaskStateCaption"] as? String
        authorId = deserializeUUIDFromMap("AuthorId", map)
        performerId = deserializeUUIDFromMap("PerformerId", map)
        taskNumber = map["TaskNumber"] as? String
        created = deserializeDateTimeFromMap("Created", map)
        stateChangedLastTime = deserializeDateTimeFromMap("StateChangedLastTime", map)
        receipt = deserializeDateTimeFromMap("Receipt", map)
        receiptActual = deserializeDateTimeFromMap("ReceiptActual", map)
        deliveryFrom = deserializeDateTimeFromMap("DeliveryFrom", map)
        deliveryTo = deserializeDateTimeFromMap("DeliveryTo", map)
        deliveryActual = deserializeDateTimeFromMap("DeliveryActual", map)
        comment = map["Comment"] as? String
        warehouseId = deserializeUUIDFromMap("WarehouseId", map)
        paymentTypeId = deserializeUUIDFromMap("PaymentTypeId", map)
        clientId = deserializeUUIDFromMap("ClientId", map)
        clientAddressId = deserializeUUIDFromMap("ClientAddressId", map)
        cost = deserializeBigDecimalFromMap("Cost", map)
        deliveryCost = deserializeBigDecimalFromMap("DeliveryCost", map)
        taskProducts = deserializeListObjectsFromMap("TaskProducts", map, { TaskProduct() })
    }
}