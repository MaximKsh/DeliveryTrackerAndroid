package com.kvteam.deliverytracker.core.common

class EventEmitter : IEventEmitter {
    private data class SubscriptionItem(
            val subscriberName: String,
            val signalName: String)

    private val signals = mutableMapOf<String, Any>()
    private val subscriptions = mutableSetOf<SubscriptionItem>()

    private val lockObj = Any()

    override fun signal(signalName: String, signalContent: Any) {
        synchronized(lockObj) {
            if(subscriptions.any { it.signalName == signalName }) {
                signals[signalName] = signalContent
            }
        }
    }

    override fun subscribe(subscriberName: String, signalName: String) {
        synchronized(lockObj) {
            subscriptions.add(SubscriptionItem(subscriberName, signalName))
        }
    }

    override fun get(subscriberName: String, signalName: String): Any? {
        synchronized(lockObj) {
            // Получаем значение сигнала или выходим, если сигнала не было
            val signalValue = signals[signalName] ?: return null
            // Проверяем, была ли подписка на данный тип сигнала от подписчика
            val hasSubscription = subscriptions.remove(SubscriptionItem(subscriberName, signalName))
            if(!hasSubscription) {
                return null
            }
            // Смотрим, остались ли еще подписчики на данный тип сигнала.
            if (!subscriptions.any { it.signalName == signalName }) {
                // Если нет, то удаляем значение сигнала.
                signals.remove(signalName)
            }
            return signalValue
        }
    }

    override fun clean() {
        synchronized(lockObj) {
            signals.clear()
            subscriptions.clear()
        }
    }

}