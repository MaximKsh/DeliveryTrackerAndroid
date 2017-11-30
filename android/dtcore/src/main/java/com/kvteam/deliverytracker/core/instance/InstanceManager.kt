package com.kvteam.deliverytracker.core.instance

import com.google.gson.reflect.TypeToken
import com.kvteam.deliverytracker.core.R
import com.kvteam.deliverytracker.core.common.*
import com.kvteam.deliverytracker.core.models.*
import com.kvteam.deliverytracker.core.storage.IStorage
import com.kvteam.deliverytracker.core.webservice.IWebservice
import com.kvteam.deliverytracker.core.webservice.NetworkResponse
import java.util.*


class InstanceManager(
        private val webservice: IWebservice,
        private val storage: IStorage,
        private val errorManager: IErrorManager,
        private val localizationManager: ILocalizationManager): IInstanceManager {
    override fun create(
            instance: InstanceModel,
            user: UserModel,
            credentials: CredentialsModel): EntityResult<UserModel?> {
        val requestObj = CreateInstanceModel(instance, credentials, user)
        val result = webservice.post<UserModel>(
                "/api/instance/create",
                requestObj,
                UserModel::class.java)
        val chain = buildChain(result)
        return EntityResult(
                result.responseEntity,
                result.fetched,
                false,
                chain)
    }

    override fun getUser(username: String): EntityResult<UserModel?> {
        val result = webservice.get<UserModel>(
                "/api/instance/get_user/{$username}",
                UserModel::class.java,
                true)
        val chain = buildChain(result)
        return EntityResult(
                result.responseEntity,
                result.fetched,
                false,
                chain)
    }

    override fun inviteManager(preliminaryUserInfo: UserModel): EntityResult<InvitationModel?> {
        val result = webservice.post<InvitationModel>(
                "/api/instance/invite_manager",
                preliminaryUserInfo,
                InvitationModel::class.java,
                true)
        val chain = buildChain(result)
        return EntityResult(
                result.responseEntity,
                result.fetched,
                false,
                chain)
    }

    override fun invitePerformer(preliminaryUserInfo: UserModel): EntityResult<InvitationModel?> {
        val result = webservice.post<InvitationModel>(
                "/api/instance/invite_performer",
                preliminaryUserInfo,
                InvitationModel::class.java,
                true)
        val chain = buildChain(result)
        return EntityResult(
                result.responseEntity,
                result.fetched,
                false,
                chain)
    }

    override fun getPerformers(resetCache: Boolean): EntityResult<List<UserModel>?> {
        val cacheKey = "performers"
        if(resetCache) {
            storage.deleteUsers(cacheKey)
        } else {
            val performers = storage.getUsers(cacheKey)
            if(performers.isNotEmpty()) {
                return EntityResult(performers, false, true, null)
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
        val chain = buildChain(result)
        return EntityResult(
                result.responseEntity,
                result.fetched,
                false,
                chain)
    }

    override fun getManagers(resetCache: Boolean): EntityResult<List<UserModel>?> {
        val cacheKey = "managers"
        if(resetCache) {
            storage.deleteUsers(cacheKey)
        } else {
            val performers = storage.getUsers(cacheKey)
            if(performers.isNotEmpty()) {
                return EntityResult(performers, false, true, null)
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
        val chain = buildChain(result)
        return EntityResult(
                result.responseEntity,
                result.fetched,
                false,
                chain)
    }

    override fun deleteManager(username: String): SimpleResult {
        val obj = UserModel(username = username)
        val result = webservice.post(
                "/api/instance/delete_manager",
                obj,
                true)
        val chain = buildChain(result)
        return SimpleResult(
                result.statusCode == 200,
                result.fetched,
                false,
                chain)
    }

    override fun deletePerformer(username: String): SimpleResult {
        val obj = UserModel(username = username)
        val result = webservice.post(
                "/api/instance/delete_performer",
                obj,
                true)
        val chain = buildChain(result)
        return SimpleResult(
                result.statusCode == 200,
                result.fetched,
                false,
                chain)
    }

    private fun <T> buildChain(nr: NetworkResponse<T>?) : UUID? {
        if(nr == null) {
            return null
        }
        val items = nr.errorList?.errors?.toList()

        val chainBuilder = errorManager
                .begin()
                .alias(localizationManager.getString(R.string.Core_InstanceManager_Error_ErrorTitle))

        if(!nr.fetched) {
            chainBuilder.add(ErrorItem(localizationManager.getString(R.string.Core_Error_NetworkError)))
        } else if (items != null){
            for(it in items) {
                chainBuilder.add(ErrorItem(localizationManager.getString(it.message)))
            }
        }
        return chainBuilder.complete()
    }

}