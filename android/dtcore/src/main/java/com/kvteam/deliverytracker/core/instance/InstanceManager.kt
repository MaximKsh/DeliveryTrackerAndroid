package com.kvteam.deliverytracker.core.instance

import com.google.gson.reflect.TypeToken
import com.kvteam.deliverytracker.core.models.*
import com.kvteam.deliverytracker.core.storage.IStorage
import com.kvteam.deliverytracker.core.webservice.*


class InstanceManager(
        private val webservice: IWebservice,
        private val storage: IStorage): IInstanceManager {
    override fun create(
            instance: InstanceModel,
            user: UserModel,
            credentials: CredentialsModel): UserModel? {
        val requestObj = CreateInstanceModel(instance, credentials, user)
        val result = webservice.post<UserModel>(
                "/api/instance/create",
                requestObj,
                UserModel::class.java)
        return result.responseEntity
    }

    override fun getUser(username: String): UserModel? {
        val result = webservice.get<UserModel>(
                "/api/instance/get_user/{$username}",
                UserModel::class.java,
                true)
        return result.responseEntity
    }

    override fun inviteManager(preliminaryUserInfo: UserModel): InvitationModel? {
        val result = webservice.post<InvitationModel>(
                "/api/instance/invite_manager",
                preliminaryUserInfo,
                InvitationModel::class.java,
                true)
        return result.responseEntity
    }

    override fun invitePerformer(preliminaryUserInfo: UserModel): InvitationModel? {
        val result = webservice.post<InvitationModel>(
                "/api/instance/invite_performer",
                preliminaryUserInfo,
                InvitationModel::class.java,
                true)
        return result.responseEntity
    }

    override fun getPerformers(resetCache: Boolean): List<UserModel>? {
        val cacheKey = "performers"
        if(resetCache) {
            storage.deleteUsers(cacheKey)
        } else {
            val performers = storage.getUsers(cacheKey)
            if(performers.isNotEmpty()) {
                return performers
            }
        }

        val result = webservice.get<List<UserModel>>(
                "/api/instance/performers",
                object : TypeToken<ArrayList<UserModel>>(){}.type,
                true)
        val newPerformers = result.responseEntity
        if(result.statusCode == 200
                && newPerformers != null) {
            storage.setUsers(cacheKey, newPerformers)
        }
        return newPerformers
    }

    override fun getManagers(resetCache: Boolean): List<UserModel>? {
        val cacheKey = "managers"
        if(resetCache) {
            storage.deleteUsers(cacheKey)
        } else {
            val performers = storage.getUsers(cacheKey)
            if(performers.isNotEmpty()) {
                return performers
            }
        }

        val result = webservice.get<List<UserModel>>(
                "/api/instance/managers",
                object : TypeToken<ArrayList<UserModel>>(){}.type,
                true)
        val newManagers = result.responseEntity
        if(result.statusCode == 200
                && newManagers != null) {
            storage.setUsers(cacheKey, newManagers)
        }
        return newManagers
    }

    override fun deleteManager(username: String): Boolean {
        val obj = UserModel(username = username)
        val result = webservice.post(
                "/api/instance/delete_manager",
                obj,
                true)
        return result.statusCode == 200
    }

    override fun deletePerformer(username: String): Boolean {
        val obj = UserModel(username = username)
        val result = webservice.post(
                "/api/instance/delete_performer",
                obj,
                true)
        return result.statusCode == 200
    }
}