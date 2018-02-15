package com.kvteam.deliverytracker.managerapp.ui.main.userslist

import android.os.Bundle
import com.kvteam.deliverytracker.core.async.invokeAsync
import com.kvteam.deliverytracker.core.models.User
import com.kvteam.deliverytracker.core.roles.Role
import com.kvteam.deliverytracker.core.webservice.IViewWebservice
import com.kvteam.deliverytracker.core.webservice.ViewWebservice
import com.kvteam.deliverytracker.managerapp.R
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_managers_list.*
import kotlinx.android.synthetic.main.toolbar.*
import java.awt.font.NumericShaper
import javax.inject.Inject

class ManagersListFragment: UsersListFragment() {
    @Inject
    lateinit var viewWebservice: IViewWebservice

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidSupportInjection.inject(this)
        super.onCreate(savedInstanceState)
        this.role = Role.Manager
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        activity!!.toolbar_title.text = resources.getString(R.string.ManagerApp_MainActivity_Managers)
        if(savedInstanceState != null
                && !ignoreSavedState) {
            return
        }
        ignoreSavedState = false

        val modelUserList = ArrayList<UserListModel>()
        for (i in 1..10) {
          val testUser = UserListModel(
                  false,
                  false,
                  User(code = "username" + i, surname = "Surname" + i, name = "Name" + i))
            modelUserList.add(testUser)
        }

//        adapter.value?.items?.addAll(modelUserList)
//        adapter.value?.notifyDataSetChanged()

//        srlSwipeRefreshUsers.setOnRefreshListener { refresh() }
//
//        invokeAsync({
//            instanceManager.getManagers()
//        }, {
//            if (it.success) {
//                val modelUserList = it.entity!!.map { userModel ->
//                    UserListModel(false,false, userModel)
//                }
//                adapter.value?.items?.addAll(modelUserList)
//                adapter.value?.notifyDataSetChanged()
//            } else {
//                /*val dialog = ErrorDialog(this@ManagersListFragment.context)
//                if(it.errorChainId != null) {
//                    dialog.addChain(errorManager.getAndRemove(it.errorChainId!!)!!)
//                }*/
//                // dialog.show()
//            }
//        })
        invokeAsync({
            viewWebservice.getViewResult("UserViewGroup", "ManagersView")
        }, {
            if (it.success) {
                val managers = it.entity?.viewResult?.map{
                    val u = User()
                    u.fromMap(it)
                    u
                }?.toList()
                val modelUserList = managers!!.map { userModel ->
                    UserListModel(false,false, userModel)
                }
//                adapter.value?.items?.addAll(modelUserList)
//                adapter.value?.notifyDataSetChanged()
            } else {
                /*val dialog = ErrorDialog(this@ManagersListFragment.context)
                if(it.errorChainId != null) {
                    dialog.addChain(errorManager.getAndRemove(it.errorChainId!!)!!)
                }*/
                // dialog.show()
            }
        })
    }

    private fun refresh() {
        invokeAsync({
            viewWebservice.getViewResult("UserViewGroup", "ManagersView")
        }, {
            if(it.success) {
                val managers = it.entity?.viewResult?.map{
                    val u = User()
                    u.fromMap(it)
                    u
                }?.toList()
//                adapter.value?.items?.clear()
                val modelUserList = managers!!.map { userModel ->
                    UserListModel(false,false, userModel)
                }
//                adapter.value?.items?.addAll(modelUserList)
//                adapter.value?.notifyDataSetChanged()
            }
//            srlSwipeRefreshUsers.isRefreshing = false
        })
    }

}
