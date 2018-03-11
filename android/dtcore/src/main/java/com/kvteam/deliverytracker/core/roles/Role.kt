package com.kvteam.deliverytracker.core.roles


import com.kvteam.deliverytracker.core.R
import com.kvteam.deliverytracker.core.common.EMPTY_STRING
import com.kvteam.deliverytracker.core.common.ILocalizationManager
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

    fun getCaption(lm: ILocalizationManager): String {
        return lm.getString(localizationStringId)
    }

    companion object {
        fun getRole(id: UUID?) : Role? {
            return when(id){
                Role.Creator.id -> Role.Creator
                Role.Manager.id -> Role.Manager
                Role.Performer.id -> Role.Performer
                else -> null
            }
        }

        fun getRole(role: String?) : Role? {
            return when(role){
                Role.Creator.roleName -> Role.Creator
                Role.Manager.roleName -> Role.Manager
                Role.Performer.roleName -> Role.Performer
                else -> null
            }
        }

        fun getCaption(id: UUID?, lm: ILocalizationManager): String {
            val role = getRole(id)
            return role?.getCaption(lm) ?: EMPTY_STRING
        }

        fun getCaption(rolename: String?, lm: ILocalizationManager): String {
            val role = getRole(rolename)
            return role?.getCaption(lm) ?: EMPTY_STRING
        }
    }

}