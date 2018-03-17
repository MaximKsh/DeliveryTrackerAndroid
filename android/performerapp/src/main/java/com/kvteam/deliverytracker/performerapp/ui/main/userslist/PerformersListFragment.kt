package com.kvteam.deliverytracker.performerapp.ui.main.userslist

import android.os.Bundle
import com.kvteam.deliverytracker.core.async.invokeAsync
import com.kvteam.deliverytracker.core.models.User
import com.kvteam.deliverytracker.core.webservice.IViewWebservice
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_users_list.*
import javax.inject.Inject

class PerformersListFragment: UsersListFragment() {
    @Inject
    lateinit var viewWebservice: IViewWebservice

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
            viewWebservice.getViewResultAsync("UserViewGroup", "PerformersView")
        }, {
            if(it.success) {
                val performers = it.entity?.viewResult?.map{
                    val u = User()
                    u.fromMap(it)
                    u
                }?.toList()
                adapter.value?.items?.addAll(performers!!)
                adapter.value?.notifyDataSetChanged()
            }
        })
        srlSwipeRefreshUsers.setOnRefreshListener { refresh() }
    }

    private fun refresh() {
        invokeAsync({
            viewWebservice.getViewResultAsync("UserViewGroup", "PerformersView")
        }, {
            if(it.success) {
                val performers = it.entity?.viewResult?.map{
                    val u = User()
                    u.fromMap(it)
                    u
                }?.toList()
                adapter.value?.items?.clear()
                adapter.value?.items?.addAll(performers!!)
                adapter.value?.notifyDataSetChanged()
            }
            srlSwipeRefreshUsers.isRefreshing = false
        })
    }
}