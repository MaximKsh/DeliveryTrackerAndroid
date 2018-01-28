package com.kvteam.deliverytracker.managerapp.ui.main.userslist

import android.os.Bundle
import com.kvteam.deliverytracker.core.async.invokeAsync
import com.kvteam.deliverytracker.core.models.User
import com.kvteam.deliverytracker.core.roles.Role
import com.kvteam.deliverytracker.core.webservice.IViewWebservice
import com.kvteam.deliverytracker.managerapp.R
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_managers_list.*
import kotlinx.android.synthetic.main.toolbar.*
import javax.inject.Inject

class PerformersListFragment: UsersListFragment() {
    @Inject
    lateinit var viewWebservice: IViewWebservice

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidSupportInjection.inject(this)
        super.onCreate(savedInstanceState)
        this.role = Role.Performer
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        this.activity!!.toolbar_title.text = resources.getString(R.string.ManagerApp_MainActivity_Performers)
        if(savedInstanceState != null
                && !ignoreSavedState) {
            return
        }
        ignoreSavedState = false

        srlSwipeRefreshUsers.setOnRefreshListener { refresh() }
        invokeAsync({
            viewWebservice.getViewResult("UserViewGroup", "PerformersView")
        }, {
            if (it.success) {
                val performers = it.entity?.viewResult?.map{
                    val u = User()
                    u.fromMap(it)
                    u
                }?.toList()
                val modelUserList = performers!!.map { userModel ->
                    UserListModel(false,false, userModel)
                }
                adapter.value?.items?.addAll(modelUserList)
                adapter.value?.notifyDataSetChanged()
            }
        })
    }

    private fun refresh() {
        invokeAsync({
            viewWebservice.getViewResult("UserViewGroup", "PerformersView")
        }, {
            if(it.success) {
                val performers = it.entity?.viewResult?.map{
                    val u = User()
                    u.fromMap(it)
                    u
                }?.toList()
                adapter.value?.items?.clear()
                val modelUserList = performers!!.map { userModel ->
                    UserListModel(false,false, userModel)
                }
                adapter.value?.items?.addAll(modelUserList)
                adapter.value?.notifyDataSetChanged()
            }
            srlSwipeRefreshUsers.isRefreshing = false
        })
    }
}
