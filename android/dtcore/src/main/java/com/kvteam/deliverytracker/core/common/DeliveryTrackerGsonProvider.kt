package com.kvteam.deliverytracker.core.common

class DeliveryTrackerGsonProvider : IDeliveryTrackerGsonProvider {
    override val gson = buildDefaultGson()
}