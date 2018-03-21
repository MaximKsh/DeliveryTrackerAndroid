package com.kvteam.deliverytracker.core.dataprovider

import com.kvteam.deliverytracker.core.models.TaskStateTransition

class TaskStateTransitionDataContainer: BaseDataContainer<TaskStateTransition>() {
    override val storageKey = "TaskStateTransition"
}