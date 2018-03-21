package com.kvteam.deliverytracker.core.storage

interface IStorage {
    fun getString (key: String) : String
    fun set (key: String, value: Any)
}