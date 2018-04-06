package com.kvteam.deliverytracker.managerapp.ui.main.taskslist

import com.kvteam.deliverytracker.core.common.EMPTY_UUID
import com.kvteam.deliverytracker.core.dataprovider.DataProviderGetMode
import com.kvteam.deliverytracker.core.dataprovider.IDataComponent
import com.kvteam.deliverytracker.core.models.ModelBase
import com.kvteam.deliverytracker.core.models.TaskInfo
import com.kvteam.deliverytracker.core.ui.ModelTextWatcher
import java.util.*

class TaskTextWatcher<T: ModelBase>(
        dataComponent: IDataComponent<T>,
        updateFunc: (T, String) -> Unit,
        private val taskId: UUID,
        private val taskComponent: IDataComponent<TaskInfo>
) : ModelTextWatcher<T>(dataComponent, EMPTY_UUID, updateFunc) {
    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        val task = taskComponent.get(taskId, DataProviderGetMode.DIRTY).entry
        id = task.clientId!!
    }
}
