package com.kvteam.deliverytracker.core.instance

import com.kvteam.deliverytracker.core.common.EntityResult
import com.kvteam.deliverytracker.core.common.SimpleResult
import com.kvteam.deliverytracker.core.models.CredentialsModel
import com.kvteam.deliverytracker.core.models.InstanceModel
import com.kvteam.deliverytracker.core.models.InvitationModel
import com.kvteam.deliverytracker.core.models.UserModel

interface IInstanceManager {
    fun create(
            instance: InstanceModel,
            user: UserModel,
            credentials: CredentialsModel): EntityResult<UserModel?>

    fun getUser(username: String): EntityResult<UserModel?>
    fun inviteManager(preliminaryUserInfo: UserModel): EntityResult<InvitationModel?>
    fun invitePerformer(preliminaryUserInfo: UserModel): EntityResult<InvitationModel?>
    fun getPerformers(resetCache: Boolean = false): EntityResult<List<UserModel>?>
    fun getManagers(resetCache: Boolean = false): EntityResult<List<UserModel>?>
    fun deleteManager(username: String): SimpleResult
    fun deletePerformer(username: String): SimpleResult
}