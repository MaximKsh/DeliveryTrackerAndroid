package com.kvteam.deliverytracker.core.dataprovider

import com.kvteam.deliverytracker.core.dataprovider.base.BaseViewComponent
import com.kvteam.deliverytracker.core.models.TaskInfo
import com.kvteam.deliverytracker.core.models.TaskPackage
import com.kvteam.deliverytracker.core.webservice.IViewWebservice
import com.kvteam.deliverytracker.core.webservice.NetworkResult
import com.kvteam.deliverytracker.core.webservice.viewmodels.ViewResponse

class TaskInfoViewComponent (
        viewWebservice: IViewWebservice,
        dataContainer: TaskInfoDataContainer
) : BaseViewComponent<TaskInfo>(viewWebservice, dataContainer) {
    override fun entryFactory() = TaskInfo()

    override fun transformViewResultToEntries(viewResult: NetworkResult<ViewResponse>): List<TaskInfo> {
        val serialized = viewResult.entity?.viewResult!!

        if (serialized.isEmpty()) {
            return listOf()
        }

        val taskInfos = mutableListOf<TaskInfo>()
        for (item in serialized) {
            val pack = TaskPackage()
            pack.fromMap(item)

            taskInfos.addAll(pack.taskInfo)
        }
        return taskInfos
    }
}