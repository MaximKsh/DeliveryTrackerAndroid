package com.kvteam.deliverytracker.core.roles

fun String.toRole(): Role? {
    return when(this){
        Role.Creator.simpleName -> Role.Creator
        Role.Manager.simpleName -> Role.Manager
        Role.Performer.simpleName -> Role.Performer
        else -> null
    }
}