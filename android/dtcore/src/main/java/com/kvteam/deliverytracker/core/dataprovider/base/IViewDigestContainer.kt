package com.kvteam.deliverytracker.core.dataprovider.base

import com.kvteam.deliverytracker.core.models.ViewDigest
import com.kvteam.deliverytracker.core.storage.IStorable

interface IViewDigestContainer : IStorable {
    fun getViewDigest(viewGroup: String): Map<String, ViewDigest>?
    fun putViewDigest(viewGroup: String, value: Map<String, ViewDigest>)
    fun removeViewDigest(viewGroup: String)
    fun clearViewDigests()

    fun recycle()
}