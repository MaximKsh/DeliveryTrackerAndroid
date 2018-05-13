package com.kvteam.deliverytracker.core.common

import com.kvteam.deliverytracker.core.models.ModelBase
import kotlin.reflect.KMutableProperty
import kotlin.reflect.full.memberProperties

@Target(AnnotationTarget.PROPERTY)
annotation class MoveAlways

@Target(AnnotationTarget.PROPERTY)
annotation class MoveIfDiffer

data class DifferenceResult <out T : ModelBase>  (
        val difference: T,
        val hasDifferentFields: Boolean
)

fun <T : ModelBase> T.getDifference(new: T, factory: () -> T) : DifferenceResult<T> {
    val diff = factory()
    var hasDifferentFields = false

    for(prop in this::class.memberProperties) {
        if(prop !is KMutableProperty<*>) {
            continue
        }
        for (annotation in prop.annotations) {
            if (annotation.annotationClass ==  MoveIfDiffer::class) {
                val oldVal = prop.getter.call(this)
                val newVal = prop.getter.call(new)
                if (oldVal != newVal) {
                    prop.setter.call(diff, newVal)
                    hasDifferentFields = true
                }
                break
            } else if (annotation.annotationClass ==  MoveAlways::class) {
                val newVal = prop.getter.call(new)
                prop.setter.call(diff, newVal)
                break
            }
        }
    }

    return DifferenceResult(diff, hasDifferentFields)
}