package com.kvteam.deliverytracker.core.instance

import com.kvteam.deliverytracker.core.models.CredentialsModel
import com.kvteam.deliverytracker.core.models.InstanceModel
import com.kvteam.deliverytracker.core.models.InvitationModel
import com.kvteam.deliverytracker.core.models.UserModel

interface IInstanceManager {
    fun create(
            instance: InstanceModel,
            user: UserModel,
            credentials: CredentialsModel): UserModel?

    fun getUser(username: String): UserModel?
    fun inviteManager(preliminaryUserInfo: UserModel): InvitationModel?
    fun invitePerformer(preliminaryUserInfo: UserModel): InvitationModel?
    fun getPerformers(resetCache: Boolean = false): List<UserModel>?
    fun getManagers(resetCache: Boolean = false): List<UserModel>?
    fun deleteManager(username: String): Boolean
    fun deletePerformer(username: String): Boolean
}