package com.kvteam.deliverytracker.core.models

import com.kvteam.deliverytracker.core.session.ISession

fun ISession.getUser() : User {
    val user = User(
        code = this.code,
            surname = this.surname,
            name = this.name,
            patronymic = this.patronymic,
            phoneNumber = this.phoneNumber,
            role = this.role)
    user.id = this.id
    user.instanceId = this.instanceId
    return user
}