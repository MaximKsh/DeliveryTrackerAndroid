package com.kvteam.deliverytracker.managerapp.ui.dropdowntop

data class DropdownTopItemInfo(
        val name: String,
        val quantity: Int,
        val onSelected: (Int) -> Unit
)
