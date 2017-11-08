package com.kvteam.deliverytracker.core.storage

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query

@Dao
interface StorageUserDao {
    @Insert
    fun insertUser(user: StorageUserEntry)

    @Insert
    fun insertUsers(users: List<StorageUserEntry>)

    @Query("delete from $USER_TABLE where cacheKey = :key")
    fun deleteUsers(key: String)

    @Query("select * from $USER_TABLE where cacheKey = :key")
    fun selectUsers(key: String): List<StorageUserEntry>

}