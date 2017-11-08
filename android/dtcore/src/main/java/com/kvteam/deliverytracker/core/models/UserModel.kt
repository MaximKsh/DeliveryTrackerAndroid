package com.kvteam.deliverytracker.core.models

import android.arch.persistence.room.Embedded

data class UserModel(
        var username: String? = null,
        var surname: String? = null,
        var name: String? = null,
        var phoneNumber: String? = null,
        var role: String? = null,
        @Embedded var instance: InstanceModel? = null,
        @Embedded var position: GeopositionModel? = null)