package com.kvteam.deliverytracker.core.webservice.viewmodels

import com.kvteam.deliverytracker.core.models.TaskPackage
import java.util.*

class TaskRequest (
        var id: UUID? = null,
        var transitionId: UUID? = null,
        var taskPackage: TaskPackage? = null
): RequestBase()