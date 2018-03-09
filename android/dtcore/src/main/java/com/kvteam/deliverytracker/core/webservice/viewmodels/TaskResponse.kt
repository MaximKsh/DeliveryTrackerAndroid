package com.kvteam.deliverytracker.core.webservice.viewmodels

import com.kvteam.deliverytracker.core.models.TaskPackage

class TaskResponse(
    var taskPackage: TaskPackage? = null
) : ResponseBase()