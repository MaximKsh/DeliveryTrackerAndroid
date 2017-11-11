package com.kvteam.deliverytracker.core.storage

import android.arch.persistence.room.Embedded
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.kvteam.deliverytracker.core.models.UserModel
import java.util.*

@Entity(tableName = USER_TABLE)
class StorageUserEntry() {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
    var cacheKey: String? = null
    var expirationDate: Date = Date()
    @Embedded
    var user: UserModel? = null

    constructor(cacheKey: String, expirationDate: Date, user: UserModel): this() {
        this.cacheKey = cacheKey
        this.expirationDate = expirationDate
        this.user = user
    }
}