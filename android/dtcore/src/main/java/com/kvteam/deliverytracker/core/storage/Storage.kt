package com.kvteam.deliverytracker.core.storage

import android.arch.persistence.room.Room
import android.content.Context
import com.kvteam.deliverytracker.core.models.TaskModel
import com.kvteam.deliverytracker.core.models.UserModel

class Storage(context: Context): IStorage {
    private val db: StorageDatabase = Room
            .databaseBuilder(context, StorageDatabase::class.java, "dt-storage")
            .build()


    override fun getUsers(key: String): List<UserModel> {
        val entries = db.userDao().selectUsers(key)
        return entries.map { it.user!! }
    }

    override fun deleteUsers(key: String) {
        db.userDao().deleteUsers(key)
    }

    override fun setUsers(key: String, users: List<UserModel>) {
        db.userDao().deleteUsers(key)
        users.forEach({db.userDao().insertUser(StorageUserEntry(key, it))})
    }

    override fun setTasks(key: String, tasks: List<TaskModel>) {
        db.userDao().deleteUsers(key)
        db.taskDao().insertTasks(tasks.map{ StorageTaskEntry(key, it )})
    }

    override fun getTasks(key: String): List<TaskModel> {
        val entries = db.taskDao().selectTasks(key)
        return entries.map { it.task!! }
    }

    override fun deleteTasks(key: String) {
        db.taskDao().deleteTasks(key)
    }

}