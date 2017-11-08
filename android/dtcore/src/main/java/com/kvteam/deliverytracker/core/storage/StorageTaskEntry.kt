package com.kvteam.deliverytracker.core.storage

import android.arch.persistence.room.Embedded
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.kvteam.deliverytracker.core.models.TaskModel

@Entity(tableName = TASK_TABLE)
class StorageTaskEntry() {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
    var cacheKey: String? = null
    @Embedded(prefix = "task_")
    var task: TaskModel? = null

    constructor(cacheKey: String, task: TaskModel): this() {
        this.cacheKey = cacheKey
        this.task = task
    }
}