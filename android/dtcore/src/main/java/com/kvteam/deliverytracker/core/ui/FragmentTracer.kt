package com.kvteam.deliverytracker.core.ui

import android.support.v4.app.Fragment
import java.util.*

class FragmentTracer {
    private val MAX_SIZE = 10
    private val queue = LinkedList<String>()

    fun next(fragment: Fragment) {
        queue.addLast(fragment::class.java.simpleName)
        if(queue.size > MAX_SIZE) {
            queue.pollFirst()
        }
    }

    fun cameFrom(name: String) : Boolean {
        if(queue.size <= 1) {
            return false
        }

        return queue[queue.size - 2] == name
    }

    fun atTheBeginning() : Boolean {
        return queue.size <= 1
    }
}