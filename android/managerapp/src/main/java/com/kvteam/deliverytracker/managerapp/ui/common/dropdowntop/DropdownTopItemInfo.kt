package com.kvteam.deliverytracker.managerapp.ui.common.dropdowntop

data class DropdownTopItemInfo(
        val viewName: String,
        val entityType: String,
        val caption: String,
        val quantity: Int,
        val onSelected: (Int) -> Unit
)
