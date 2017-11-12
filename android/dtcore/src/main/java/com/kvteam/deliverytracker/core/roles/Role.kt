package com.kvteam.deliverytracker.core.roles


import com.kvteam.deliverytracker.core.R


enum class Role {
    Creator {
        override val simpleName = "CreatorRole"
        override val localizationStringId = R.string.Core_Role_CreatorRole
        override val localizationStringName = "Core_Role_CreatorRole"
    },

    Manager {
        override val simpleName = "ManagerRole"
        override val localizationStringId = R.string.Core_Role_ManagerRole
        override val localizationStringName = "Core_Role_ManagerRole"
    },

    Performer {
        override val simpleName = "PerformerRole"
        override val localizationStringId = R.string.Core_Role_PerformerRole
        override val localizationStringName = "Core_Role_PerformerRole"
    },
    ;

    abstract val simpleName: String
    abstract val localizationStringId: Int
    abstract val localizationStringName: String
}