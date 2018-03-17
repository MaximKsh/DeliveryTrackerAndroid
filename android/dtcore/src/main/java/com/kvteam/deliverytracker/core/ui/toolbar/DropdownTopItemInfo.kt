package com.kvteam.deliverytracker.core.ui.toolbar

data class DropdownTopItemInfo(
        val viewName: String,
        val entityType: String,
        val caption: String,
        val quantity: Int,
        val onSelected: suspend (Int) -> Unit
)
