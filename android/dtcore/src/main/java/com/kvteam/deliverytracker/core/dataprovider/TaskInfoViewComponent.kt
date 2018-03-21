package com.kvteam.deliverytracker.core.dataprovider

import com.kvteam.deliverytracker.core.models.TaskInfo
import com.kvteam.deliverytracker.core.webservice.IViewWebservice

class TaskInfoViewComponent (
        viewWebservice: IViewWebservice,
        dataContainer: TaskInfoDataContainer
) : BaseViewComponent<TaskInfo>(viewWebservice, dataContainer) {
    override fun entryFactory() = TaskInfo()
}