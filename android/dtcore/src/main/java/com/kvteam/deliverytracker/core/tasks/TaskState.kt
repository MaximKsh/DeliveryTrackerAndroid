package com.kvteam.deliverytracker.core.tasks

import com.kvteam.deliverytracker.core.R

enum class TaskState {
    NewUndistributed {
        override val simpleName = "TaskState_NewUndistributed"
        override val localizationStringId = R.string.Core_TaskState_NewUndistributed
        override val localizationStringName = "Core_TaskState_NewUndistributed"
    },

    New {
        override val simpleName = "TaskState_New"
        override val localizationStringId = R.string.Core_TaskState_New
        override val localizationStringName = "Core_TaskState_New"
    },

    InWork {
        override val simpleName = "TaskState_InWork"
        override val localizationStringId = R.string.Core_TaskState_InWork
        override val localizationStringName = "Core_TaskState_InWork"
    },

    Performed {
        override val simpleName = "TaskState_Performed"
        override val localizationStringId = R.string.Core_TaskState_Performed
        override val localizationStringName = "Core_TaskState_Performed"
    },

    Cancelled {
        override val simpleName = "TaskState_Cancelled"
        override val localizationStringId = R.string.Core_TaskState_Cancelled
        override val localizationStringName = "Core_TaskState_Cancelled"
    },

    CancelledByManager {
        override val simpleName = "TaskState_CancelledByManager"
        override val localizationStringId = R.string.Core_TaskState_CancelledByManager
        override val localizationStringName = "Core_TaskState_CancelledByManager"
    }
    ;

    abstract val simpleName: String
    abstract val localizationStringId: Int
    abstract val localizationStringName: String
}