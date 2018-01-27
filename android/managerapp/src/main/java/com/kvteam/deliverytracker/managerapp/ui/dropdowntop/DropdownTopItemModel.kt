package com.kvteam.deliverytracker.managerapp.ui.dropdowntop

data class DropdownItem (
        val name: String,
        val quantity: Int,
        val onSelected: () -> Unit
)
