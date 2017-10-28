package com.kvteam.deliverytracker.core.tasks

import com.kvteam.deliverytracker.core.R

enum class TaskState {
    NewUndistributed {
        override fun toString() = "TaskState_NewUndistributed"
        override fun getLocalizationId() = R.string.Core_TaskState_NewUndistributed
        override fun getLocalizationName() = "Core_TaskState_NewUndistributed"
    },

    New {
        override fun toString() = "TaskState_New"
        override fun getLocalizationId() = R.string.Core_TaskState_New
        override fun getLocalizationName() = "Core_TaskState_New"
    },

    InWork {
        override fun toString() = "TaskState_InWork"
        override fun getLocalizationId() = R.string.Core_TaskState_InWork
        override fun getLocalizationName() = "Core_TaskState_InWork"
    },

    Performed {
        override fun toString() = "TaskState_Performed"
        override fun getLocalizationId() = R.string.Core_TaskState_Performed
        override fun getLocalizationName() = "Core_TaskState_Performed"
    },

    Cancelled {
        override fun toString() = "TaskState_Cancelled"
        override fun getLocalizationId() = R.string.Core_TaskState_Cancelled
        override fun getLocalizationName() = "Core_TaskState_Cancelled"
    },

    CancelledByManager {
        override fun toString() = "TaskState_CancelledByManager"
        override fun getLocalizationId() = R.string.Core_TaskState_CancelledByManager
        override fun getLocalizationName() = "Core_TaskState_CancelledByManager"
    }
    ;

    abstract fun getLocalizationId(): Int
    abstract fun getLocalizationName(): String

    fun getByName(name: String): TaskState? {
        return when(name){
            NewUndistributed.toString() -> NewUndistributed
            New.toString() -> New
            InWork.toString() -> InWork
            Performed.toString() -> Performed
            Cancelled.toString() -> Cancelled
            CancelledByManager.toString() -> CancelledByManager
            else -> null
        }
    }
}