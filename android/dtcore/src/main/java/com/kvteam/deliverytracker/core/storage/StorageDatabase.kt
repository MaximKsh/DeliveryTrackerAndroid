package com.kvteam.deliverytracker.core.storage

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters

@Database(entities = arrayOf(StorageUserEntry::class, StorageTaskEntry::class), version = 1)
@TypeConverters(Converters::class)
abstract class StorageDatabase : RoomDatabase() {
    abstract fun userDao(): StorageUserDao

    abstract fun taskDao(): StorageTaskDao
}