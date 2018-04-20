package com.kvteam.deliverytracker.managerapp.ui.main.taskslist

import org.joda.time.DateTime

data class DeliveryDateTypeItem(
        var name: String,
        var selectedDateTime: DateTime?
)
