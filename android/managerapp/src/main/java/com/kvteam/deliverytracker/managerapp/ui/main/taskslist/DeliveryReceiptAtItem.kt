package com.kvteam.deliverytracker.managerapp.ui.main.taskslist

import org.joda.time.DateTime


data class DeliveryReceiptAtItem(
        var name: String,
        var selectedDateTime: DateTime?
)