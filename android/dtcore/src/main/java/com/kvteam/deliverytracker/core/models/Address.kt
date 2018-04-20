package com.kvteam.deliverytracker.core.models

data class Address (
        override var geoposition: Geoposition?
) : IAddress