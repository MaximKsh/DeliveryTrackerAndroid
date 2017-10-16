package com.kvteam.deliverytracker.core.webservice

/**
 * Created by maxim on 14.10.17.
 */

data class UserInfoModel(val userName: String,
                         val displayableName: String,
                         val group: String,
                         val role: String,
                         val position: GeopositionModel)