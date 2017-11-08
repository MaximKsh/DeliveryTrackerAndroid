package com.kvteam.deliverytracker.core.storage

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query

@Dao
interface StorageTaskDao {
    @Insert
    fun insertTask(user: StorageTaskEntry)

    @Insert
    fun insertTasks(users: List<StorageTaskEntry>)

    @Query("delete from $TASK_TABLE where cacheKey = :key")
    fun deleteTasks(key: String)

    @Query("select * from $TASK_TABLE where cacheKey = :key")
    fun selectTasks(key: String): List<StorageTaskEntry>

}