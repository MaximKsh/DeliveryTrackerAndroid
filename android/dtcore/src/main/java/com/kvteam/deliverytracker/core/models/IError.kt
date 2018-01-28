package com.kvteam.deliverytracker.core.models

interface IError {
    var code: String
    var message: String
    var info: Map<String, String>
}