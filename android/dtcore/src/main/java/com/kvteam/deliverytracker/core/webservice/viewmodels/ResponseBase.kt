package com.kvteam.deliverytracker.core.webservice.viewmodels

import com.kvteam.deliverytracker.core.models.WebserviceError

abstract class ResponseBase (var errors : List<WebserviceError> = listOf())