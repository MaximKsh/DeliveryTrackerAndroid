package com.kvteam.deliverytracker.core.dataprovider

import com.kvteam.deliverytracker.core.dataprovider.base.BaseCollectionDataContainer
import com.kvteam.deliverytracker.core.models.TaskProduct

class TaskProductDataContainer : BaseCollectionDataContainer<TaskProduct>() {
    override val storageKey: String
        get() = "TaskProductKey"

}