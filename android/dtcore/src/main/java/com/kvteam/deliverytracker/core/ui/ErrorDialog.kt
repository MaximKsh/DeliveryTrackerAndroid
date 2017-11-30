package com.kvteam.deliverytracker.core.ui

import android.content.Context
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import com.kvteam.deliverytracker.core.R
import com.kvteam.deliverytracker.core.common.ErrorChain

class ErrorDialog
constructor(private val context: Context) {
    private val chains = mutableListOf<ErrorChain>()

    fun addChain(chain: ErrorChain) : ErrorDialog {
        chains.add(chain)
        return this
    }

    fun addChains(chains: List<ErrorChain>) : ErrorDialog{
        this.chains.addAll(chains)
        return this
    }

    fun show() {
        val rv = RecyclerView(context)
        rv.adapter = ErrorListAdapter(chains)
        rv.layoutManager = LinearLayoutManager(
                context,
                LinearLayoutManager.VERTICAL,
                false)

        AlertDialog.Builder(context)
                .setView(rv)
                .setPositiveButton(R.string.Core_OK, { _, _ -> })
                .show()
    }

}