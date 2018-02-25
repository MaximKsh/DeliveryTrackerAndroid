package com.kvteam.deliverytracker.core.roles


import com.kvteam.deliverytracker.core.R
import java.util.*


enum class Role {
    Creator {
        override val id = UUID.fromString("fbe65847-57c0-42a9-84a9-3f95b92fd39e")!!
        override val roleName = "CreatorRole"
        override val localizationStringId = R.string.ServerMessage_Roles_CreatorRole
        override val localizationStringName = "ServerMessage_Roles_CreatorRole"
    },

    Manager {
        override val id = UUID.fromString("ca4e3a74-86bb-4c6e-84b5-9e2da47d1b2e")!!
        override val roleName = "ManagerRole"
        override val localizationStringId = R.string.ServerMessage_Roles_ManagerRole
        override val localizationStringName = "ServerMessage_Roles_ManagerRole"
    },

    Performer {
        override val id = UUID.fromString("aa522dd3-3a11-46a0-afa7-260b70609524")!!
        override val roleName = "PerformerRole"
        override val localizationStringId = R.string.ServerMessage_Roles_PerformerRole
        override val localizationStringName = "ServerMessage_Roles_PerformerRole"
    },
    ;

    abstract val id: UUID
    abstract val roleName: String
    abstract val localizationStringId: Int
    abstract val localizationStringName: String
}