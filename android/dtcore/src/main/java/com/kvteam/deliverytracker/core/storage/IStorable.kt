package com.kvteam.deliverytracker.core.storage

interface IStorable {
    fun fromStorage(storage: IStorage)
    fun toStorage(storage: IStorage)
}