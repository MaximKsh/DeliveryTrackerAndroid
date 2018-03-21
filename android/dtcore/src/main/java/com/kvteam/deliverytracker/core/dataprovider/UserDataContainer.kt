package com.kvteam.deliverytracker.core.dataprovider

import com.kvteam.deliverytracker.core.models.User
class UserDataContainer : BaseDataContainer<User>() {
    override val storageKey = "User"
}