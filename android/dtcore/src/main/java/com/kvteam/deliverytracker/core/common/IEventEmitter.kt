package com.kvteam.deliverytracker.core.common

interface IEventEmitter {
    fun signal (signalName: String, signalContent: Any)
    fun subscribe (subscriberName: String, signalName: String)
    fun get (subscriberName: String, signalName: String): Any?

    fun clean()
}