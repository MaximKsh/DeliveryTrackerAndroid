package com.kvteam.deliverytracker.core.dagger.keys


import android.arch.lifecycle.ViewModel
import kotlin.annotation.Retention
import dagger.MapKey
import kotlin.annotation.Target
import kotlin.reflect.KClass

@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER)
@Retention(AnnotationRetention.RUNTIME)
@MapKey
annotation class ViewModelKey(val value: KClass<out ViewModel>)
