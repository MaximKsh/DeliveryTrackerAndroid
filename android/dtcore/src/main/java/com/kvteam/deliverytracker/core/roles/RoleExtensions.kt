package com.kvteam.deliverytracker.core.roles

import java.util.*

fun UUID.toRole(): Role? {
    return when(this){
        Role.Creator.id -> Role.Creator
        Role.Manager.id -> Role.Manager
        Role.Performer.id -> Role.Performer
        else -> null
    }
}

fun String.toRole(): Role? {
    return when(this){
        Role.Creator.roleName -> Role.Creator
        Role.Manager.roleName -> Role.Manager
        Role.Performer.roleName -> Role.Performer
        else -> null
    }
}