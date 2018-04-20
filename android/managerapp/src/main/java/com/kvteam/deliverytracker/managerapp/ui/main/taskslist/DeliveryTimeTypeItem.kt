package com.kvteam.deliverytracker.managerapp.ui.main.taskslist

import org.joda.time.DateTime


data class DeliveryTimeTypeItem(
        var name: String,
        var fromTime: DateTime?,
        var toTime: DateTime?
)