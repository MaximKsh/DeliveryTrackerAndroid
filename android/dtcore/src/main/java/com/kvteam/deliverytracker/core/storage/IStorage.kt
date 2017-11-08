package com.kvteam.deliverytracker.core.storage

import com.kvteam.deliverytracker.core.models.TaskModel
import com.kvteam.deliverytracker.core.models.UserModel

interface IStorage {
    fun setUsers(key: String, users: List<UserModel>)
    fun getUsers(key: String): List<UserModel>
    fun deleteUsers(key: String)

    fun setTasks(key: String, tasks: List<TaskModel>)
    fun getTasks(key: String): List<TaskModel>
    fun deleteTasks(key: String)
}