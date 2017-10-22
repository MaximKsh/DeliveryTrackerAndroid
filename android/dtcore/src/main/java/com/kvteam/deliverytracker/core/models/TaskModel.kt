package com.kvteam.deliverytracker.core.models

import java.util.*

/**
 * Created by maxim on 20.10.17.
 */

data class TaskModel(var id: UUID? = null,
                     var number: String? = null,
                     var details: String? = null,
                     var shippingDesc: String? = null,
                     var address: String? = null,
                     var state: String? = null,
                     var instance: InstanceModel? = null,
                     var author: UserModel? = null,
                     var performer: UserModel? = null,
                     var taskDateTimeRange: DateTimeRangeModel? = null,
                     var creationDate: Date? = null,
                     var setPerformerDate: Date? = null,
                     var inWorkDate: Date? = null,
                     var completionDate: Date? = null)