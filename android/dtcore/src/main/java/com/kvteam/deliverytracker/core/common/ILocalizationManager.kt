package com.kvteam.deliverytracker.core.common

interface ILocalizationManager {
    fun getString(resId: Int): String
    fun getString(resName: String): String
}