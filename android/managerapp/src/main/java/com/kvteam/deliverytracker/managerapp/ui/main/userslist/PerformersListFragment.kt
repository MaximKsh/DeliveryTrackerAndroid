package com.kvteam.deliverytracker.managerapp.ui.main.userslist

import android.os.Bundle
import com.kvteam.deliverytracker.core.async.invokeAsync
import com.kvteam.deliverytracker.core.instance.IInstanceManager
import com.kvteam.deliverytracker.core.roles.Role
import com.kvteam.deliverytracker.managerapp.R
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_managers_list.*
import kotlinx.android.synthetic.main.toolbar.*
import javax.inject.Inject

class PerformersListFragment: UsersListFragment() {
    @Inject
    lateinit var instanceManager: IInstanceManager

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidSupportInjection.inject(this)
        super.onCreate(savedInstanceState)
        this.role = Role.Performer
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if(savedInstanceState != null) {
            return
        }

        this.activity.toolbar_title.text = resources.getString(R.string.ManagerApp_MainActivity_Performers)
        srlSwipeRefreshUsers.setOnRefreshListener { refresh() }
        invokeAsync({
            instanceManager.getPerformers(true)
        }, {
            if (it != null) {
                val modelUserList = it.map { userModel ->
                    UserListModel(false,false, userModel)
                }
                adapter.value?.items?.addAll(modelUserList)
                adapter.value?.notifyDataSetChanged()
            }
        })
    }

    private fun refresh() {
        invokeAsync({
            instanceManager.getPerformers(true)
        }, {
            if(it != null) {
                adapter.value?.items?.clear()
                val modelUserList = it.map { userModel ->
                    UserListModel(false,false, userModel)
                }
                adapter.value?.items?.addAll(modelUserList)
                adapter.value?.notifyDataSetChanged()
            }
            srlSwipeRefreshUsers.isRefreshing = false
        })
    }
}
