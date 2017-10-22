package com.kvteam.deliverytracker.core.models

/**
 * Created by maxim on 14.10.17.
 */

data class UserModel(var username: String? = null,
                     var surname: String? = null,
                     var name: String? = null,
                     var phoneNumber: String? = null,
                     var role: String? = null,
                     var instance: InstanceModel? = null,
                     var position: GeopositionModel? = null)