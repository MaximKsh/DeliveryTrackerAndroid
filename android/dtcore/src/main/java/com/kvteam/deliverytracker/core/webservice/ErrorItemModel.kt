package com.kvteam.deliverytracker.core.webservice

/**
 * Created by maxim on 15.10.17.
 */

data class ErrorItemModel(val code: String,
                          val message: String,
                          val info: Map<String, String>)