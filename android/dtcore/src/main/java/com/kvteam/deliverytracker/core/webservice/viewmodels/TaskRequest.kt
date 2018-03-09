package com.kvteam.deliverytracker.core.webservice.viewmodels

import com.kvteam.deliverytracker.core.models.TaskInfo
import java.util.*

class TaskRequest (
        var id: UUID? = null,
        var taskInfo: TaskInfo? = null
): RequestBase()