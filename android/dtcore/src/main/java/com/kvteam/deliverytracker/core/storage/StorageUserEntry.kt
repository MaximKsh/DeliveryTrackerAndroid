package com.kvteam.deliverytracker.core.storage

import android.arch.persistence.room.Embedded
import android.arch.persistence.room.Entity
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.PrimaryKey
import com.kvteam.deliverytracker.core.models.User
import java.util.*

@Entity(tableName = USER_TABLE)
class StorageUserEntry() {
    @PrimaryKey(autoGenerate = true)
    var rowId: Int = 0
    var cacheKey: String? = null
    var expirationDate: Date = Date()
    @Embedded
    var user: User? = null

    @Ignore
    constructor(cacheKey: String, expirationDate: Date, user: User): this() {
        this.cacheKey = cacheKey
        this.expirationDate = expirationDate
        this.user = user
    }
}