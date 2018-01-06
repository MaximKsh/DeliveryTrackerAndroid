package com.kvteam.deliverytracker.performerapp.ui.main.userslist

import android.os.Bundle
import com.kvteam.deliverytracker.core.async.invokeAsync
import com.kvteam.deliverytracker.core.common.IErrorManager
import com.kvteam.deliverytracker.core.instance.IInstanceManager
import com.kvteam.deliverytracker.core.ui.ErrorDialog
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_users_list.*
import javax.inject.Inject

class PerformersListFragment: UsersListFragment() {
    @Inject
    lateinit var instanceManager: IInstanceManager

    @Inject
    lateinit var errorManager: IErrorManager

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidSupportInjection.inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if(savedInstanceState != null
                && !ignoreSavedState) {
            return
        }
        ignoreSavedState = false

        invokeAsync({
            instanceManager.getPerformers()
        }, {
            if(it.success) {
                adapter.value?.items?.addAll(it.entity!!)
                adapter.value?.notifyDataSetChanged()
            } else {
                val dialog = ErrorDialog(this@PerformersListFragment.context!!)
                if(it.errorChainId != null) {
                    dialog.addChain(errorManager.getAndRemove(it.errorChainId!!)!!)
                }
                dialog.show()
            }
        })
        srlSwipeRefreshUsers.setOnRefreshListener { refresh() }
    }

    private fun refresh() {
        invokeAsync({
            instanceManager.getPerformers(true)
        }, {
            if(it.success) {
                adapter.value?.items?.clear()
                adapter.value?.items?.addAll(it.entity!!)
                adapter.value?.notifyDataSetChanged()
            } else {
                val dialog = ErrorDialog(this@PerformersListFragment.context!!)
                if(it.errorChainId != null) {
                    dialog.addChain(errorManager.getAndRemove(it.errorChainId!!)!!)
                }
                dialog.show()
            }
            srlSwipeRefreshUsers.isRefreshing = false
        })
    }
}