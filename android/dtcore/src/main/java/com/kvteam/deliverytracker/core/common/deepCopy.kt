package com.kvteam.deliverytracker.core.common


val gsonForCopy = buildDefaultGson()
fun <T : Any> T.deepCopy() : T  {
    return gsonForCopy.fromJson(gsonForCopy.toJson(this), this.javaClass)
}