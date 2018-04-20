package com.kvteam.deliverytracker.core.models

data class Address (
        override var rawAddress: String?,
        override var geoposition: Geoposition?
) : IAddress