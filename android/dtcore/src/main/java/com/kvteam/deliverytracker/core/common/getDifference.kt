package com.kvteam.deliverytracker.core.common

import com.kvteam.deliverytracker.core.models.ModelBase
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.jvm.javaField

annotation class MoveAlways

annotation class DiffMember

fun <T : ModelBase> T.getDifference(new: T, factory: () -> T) : T {
    val annotatedProperties = this::class
            .declaredMemberProperties
            .filter { it.findAnnotation<DiffMember>() != null }
    val diff = factory()

    for(prop in annotatedProperties) {
        val field = prop.javaField!!
        val oldVal = field.get(this)
        val newVal = field.get(new)
        if(oldVal != newVal) {
            field.set(diff, newVal)
        }
    }

    val mustHaveProperties = this::class
            .declaredMemberProperties
            .filter { it.findAnnotation<MoveAlways>() != null }

    for(prop in mustHaveProperties) {
        val field = prop.javaField!!
        val value = field.get(this)
        field.set(diff, value)
    }
    // TODO: Дифы с учетом вложенных коллекций
    return new
}