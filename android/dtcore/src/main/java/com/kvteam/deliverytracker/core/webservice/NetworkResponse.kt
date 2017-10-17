package com.kvteam.deliverytracker.core.webservice

data class NetworkResponse <out T>(
        val responseEntity: T?,
        val errorList: ErrorListModel?)