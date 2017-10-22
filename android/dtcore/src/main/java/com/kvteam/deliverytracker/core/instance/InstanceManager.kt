package com.kvteam.deliverytracker.core.instance

import com.google.gson.reflect.TypeToken
import com.kvteam.deliverytracker.core.models.*
import com.kvteam.deliverytracker.core.webservice.*


class InstanceManager(
        private val webservice: IWebservice): IInstanceManager {
    override fun create(
            instance: InstanceModel,
            user: UserModel,
            credentials: CredentialsModel): UserModel? {
        val requestObj = CreateInstanceModel(instance, credentials, user)
        val result = this.webservice.post<UserModel>(
                "/api/instance/create",
                requestObj,
                UserModel::class.java)
        return result.responseEntity
    }

    override fun getUser(username: String): UserModel? {
        val result = this.webservice.get<UserModel>(
                "/api/instance/get_user/{$username}",
                UserModel::class.java,
                true)
        return result.responseEntity
    }

    override fun inviteManager(preliminaryUserInfo: UserModel): InvitationModel? {
        val result = this.webservice.post<InvitationModel>(
                "/api/instance/invite_manager",
                preliminaryUserInfo,
                InvitationModel::class.java,
                true)
        return result.responseEntity
    }

    override fun invitePerformer(preliminaryUserInfo: UserModel): InvitationModel? {
        val result = this.webservice.post<InvitationModel>(
                "/api/instance/invite_performer",
                preliminaryUserInfo,
                InvitationModel::class.java,
                true)
        return result.responseEntity
    }

    override fun getPerformers(): List<UserModel>? {
        val result = this.webservice.get<List<UserModel>>(
                "/api/instance/performers",
                object : TypeToken<ArrayList<UserModel>>(){}.type,
                true)
        return result.responseEntity
    }

    override fun getManagers(): List<UserModel>? {
        val result = this.webservice.get<List<UserModel>>(
                "/api/instance/managers",
                object : TypeToken<ArrayList<UserModel>>(){}.type,
                true)
        return result.responseEntity
    }

    override fun deleteManager(username: String): Boolean {
        val obj = UserModel(username = username)
        val result = this.webservice.post(
                "/api/instance/delete_manager",
                obj,
                true)
        return result.statusCode == 200
    }

    override fun deletePerformer(username: String): Boolean {
        val obj = UserModel(username = username)
        val result = this.webservice.post(
                "/api/instance/delete_performer",
                obj,
                true)
        return result.statusCode == 200
    }
}