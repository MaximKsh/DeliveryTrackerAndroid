package com.kvteam.deliverytracker.core.tasks

import com.kvteam.deliverytracker.core.R
import java.util.*

enum class TaskState {
    Preparing {
        override val id: UUID = UUID.fromString("8c9c1011-f7c1-4cef-902f-4925f5e83f4a")
        override val stateName: String = "Preparing"
        override val stateCaption: String = "ServerMessage_TaskStates_Preparing"
        override val color: Int = R.color.colorBlueGray
    },

    Queue {
        override val id: UUID = UUID.fromString("d4595da3-6a5f-4455-b975-7637ea429cb5")
        override val stateName: String = "Queue"
        override val stateCaption: String = "ServerMessage_TaskStates_Queue"
        override val color: Int = R.color.colorYellow
    },

    Waiting {
        override val id: UUID = UUID.fromString("0a79703f-4570-4a58-8509-9e598b1eefaf")
        override val stateName: String = "Waiting"
        override val stateCaption: String = "ServerMessage_TaskStates_Waiting"
        override val color: Int = R.color.colorYellow
    },

    IntoWork {
        override val id: UUID = UUID.fromString("8912d18f-192a-4327-bd47-5c9963b5f2b0")
        override val stateName: String = "IntoWork"
        override val stateCaption: String = "ServerMessage_TaskStates_IntoWork"
        override val color: Int = R.color.colorYellow
    },

    Delivered {
        override val id: UUID = UUID.fromString("020d7c7e-bb4e-4add-8b11-62a91471b7c8")
        override val stateName: String = "Delivered"
        override val stateCaption: String = "ServerMessage_TaskStates_Delivered"
        override val color: Int = R.color.colorBlue
    },

    Complete {
        override val id: UUID = UUID.fromString("d91856f9-d1bf-4fad-a46e-c3baafabf762")
        override val stateName: String = "Complete"
        override val stateCaption: String = "ServerMessage_TaskStates_Complete"
        override val color: Int = R.color.colorGreen
    },

    Revoked {
        override val id: UUID = UUID.fromString("d2e70369-3d37-420f-b176-5fa0b2c1d4a9")
        override val stateName: String = "Revoked"
        override val stateCaption: String = "ServerMessage_TaskStates_Revoked"
        override val color: Int = R.color.colorRed
    }
    ;

    abstract val id: UUID
    abstract val stateName: String
    abstract val stateCaption: String
    abstract val color: Int
}