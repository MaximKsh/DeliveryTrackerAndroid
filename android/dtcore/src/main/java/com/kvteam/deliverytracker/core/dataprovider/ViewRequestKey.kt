package com.kvteam.deliverytracker.core.dataprovider

data class ViewRequestKey (
        val viewGroup: String,
        val viewName: String,
        val viewParams: Map<String, Any>?)