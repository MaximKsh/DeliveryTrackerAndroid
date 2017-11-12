package com.kvteam.deliverytracker.performerapp.ui.main.userslist

import android.os.Bundle
import com.kvteam.deliverytracker.core.async.invokeAsync
import com.kvteam.deliverytracker.core.instance.IInstanceManager
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_users_list.*
import javax.inject.Inject

class PerformersListFragment: UsersListFragment() {
    @Inject
    lateinit var instanceManager: IInstanceManager

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
            if(it != null) {
                adapter.value?.items?.addAll(it)
                adapter.value?.notifyDataSetChanged()
            }
        })
        srlSwipeRefreshUsers.setOnRefreshListener { refresh() }
    }

    private fun refresh() {
        invokeAsync({
            instanceManager.getPerformers(true)
        }, {
            if(it != null) {
                adapter.value?.items?.clear()
                adapter.value?.items?.addAll(it)
                adapter.value?.notifyDataSetChanged()
            }
            srlSwipeRefreshUsers.isRefreshing = false
        })
    }
}