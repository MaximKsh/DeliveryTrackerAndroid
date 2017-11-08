package com.kvteam.deliverytracker.core.models

import android.arch.persistence.room.Embedded
import java.util.*

data class TaskModel(
        var id: UUID? = null,
        var number: String? = null,
        var details: String? = null,
        var shippingDesc: String? = null,
        var address: String? = null,
        var state: String? = null,
        @Embedded(prefix = "instance_") var instance: InstanceModel? = null,
        @Embedded(prefix = "author_") var author: UserModel? = null,
        @Embedded(prefix = "performer_") var performer: UserModel? = null,
        @Embedded var taskDateTimeRange: DateTimeRangeModel? = null,
        var creationDate: Date? = null,
        var setPerformerDate: Date? = null,
        var inWorkDate: Date? = null,
        var completionDate: Date? = null)