package com.kvteam.deliverytracker.core.common


open class EntityResult<T : Any?>: ErrorListResult() {
    open var entity : T? = null

}