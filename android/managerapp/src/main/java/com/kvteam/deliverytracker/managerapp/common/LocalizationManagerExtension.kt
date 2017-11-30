package com.kvteam.deliverytracker.managerapp.common

import com.kvteam.deliverytracker.core.common.ILocalizationManagerExtension
import com.kvteam.deliverytracker.managerapp.R

class LocalizationManagerExtension: ILocalizationManagerExtension {
    override val types: List<Class<out Any>> = listOf(R.string::class.java)
}