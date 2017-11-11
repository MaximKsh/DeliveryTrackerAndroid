package com.kvteam.deliverytracker.core.storage

import android.arch.persistence.room.Room
import android.content.Context
import com.kvteam.deliverytracker.core.models.TaskModel
import com.kvteam.deliverytracker.core.models.UserModel
import java.util.*

class Storage(context: Context): IStorage {
    // 1 минута
    private val defaultExpirationMs: Long = 60 * 1000

    private val db: StorageDatabase = Room
            .databaseBuilder(context, StorageDatabase::class.java, "dt-storage")
            .build()

    override fun setUsers(key: String, users: List<UserModel>, expirationMs: Long?) {
        db.userDao().deleteUsers(key)
        val expirationDate = Date(
                Calendar.getInstance().timeInMillis + (expirationMs ?: defaultExpirationMs))
        users.forEach({db.userDao().insertUser(StorageUserEntry(key, expirationDate, it))})
    }

    override fun getUsers(key: String): List<UserModel> {
        val entries = db.userDao().selectUsers(key)
        val currentDate = Date()
        if(entries.any { it.expirationDate.before(currentDate) }) {
            // Просроченые данные
            deleteUsers(key)
            return listOf()
        }
        return entries.map { it.user!! }
    }

    override fun deleteUsers(key: String) {
        db.userDao().deleteUsers(key)
    }

    override fun setTasks(key: String, tasks: List<TaskModel>, expirationMs: Long?) {
        db.userDao().deleteUsers(key)
        val expirationDate = Date(
                Calendar.getInstance().timeInMillis + (expirationMs ?: defaultExpirationMs))
        db.taskDao().insertTasks(tasks.map{ StorageTaskEntry(key, expirationDate, it )})
    }

    override fun getTasks(key: String): List<TaskModel> {
        val entries = db.taskDao().selectTasks(key)
        val currentDate = Date()
        if(entries.any { it.expirationDate.before(currentDate) }) {
            // Просроченые данные
            deleteTasks(key)
            return listOf()
        }
        return entries.map { it.task!! }
    }

    override fun deleteTasks(key: String) {
        db.taskDao().deleteTasks(key)
    }

}