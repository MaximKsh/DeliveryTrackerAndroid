package com.kvteam.deliverytracker.core.storage

import com.kvteam.deliverytracker.core.models.TaskModel
import com.kvteam.deliverytracker.core.models.User

interface IStorage {
    fun setUsers(key: String, users: List<User>, expirationMs: Long? = null)
    fun getUsers(key: String): List<User>
    fun deleteUsers(key: String)

    fun setTasks(key: String, tasks: List<TaskModel>, expirationMs: Long? = null)
    fun getTasks(key: String): List<TaskModel>
    fun deleteTasks(key: String)
}