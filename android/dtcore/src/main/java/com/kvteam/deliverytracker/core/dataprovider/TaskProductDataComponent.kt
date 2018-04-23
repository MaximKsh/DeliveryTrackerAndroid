package com.kvteam.deliverytracker.core.dataprovider

import com.kvteam.deliverytracker.core.dataprovider.base.BaseCollectionDataComponent
import com.kvteam.deliverytracker.core.models.TaskProduct

class TaskProductDataComponent (
        dataContainer: TaskProductDataContainer
) : BaseCollectionDataComponent<TaskProduct>(dataContainer) {
    override fun entryFactory(): TaskProduct {
        return TaskProduct()
    }

}