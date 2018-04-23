package com.kvteam.deliverytracker.core.models

interface IAddress {
    var rawAddress: String?
    var geoposition: Geoposition?
}