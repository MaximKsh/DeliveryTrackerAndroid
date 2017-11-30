package com.kvteam.deliverytracker.performerapp.common

import com.kvteam.deliverytracker.core.common.ILocalizationManagerExtension
import com.kvteam.deliverytracker.performerapp.R

class LocalizationManagerExtension: ILocalizationManagerExtension {
    override val types: List<Class<out Any>> = listOf(R.string::class.java)
}