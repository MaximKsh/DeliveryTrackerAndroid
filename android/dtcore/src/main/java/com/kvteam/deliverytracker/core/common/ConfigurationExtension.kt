package com.kvteam.deliverytracker.core.common

val Configuration.webserviceURL: String
    get() = this.get("webserviceURL")