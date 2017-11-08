package com.kvteam.deliverytracker.core.storage

import android.arch.persistence.room.Embedded
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.kvteam.deliverytracker.core.models.UserModel

@Entity(tableName = USER_TABLE)
class StorageUserEntry() {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
    var cacheKey: String? = null
    @Embedded
    var user: UserModel? = null

    constructor(cacheKey: String, user: UserModel): this() {
        this.cacheKey = cacheKey
        this.user = user
    }
}