package com.kvteam.deliverytracker.core.ui.dropdownselect

data class DropdownSelectItem<out T>(
        val data: T,
        val isLink: Boolean = false,
        var isDisabled: Boolean = false
)