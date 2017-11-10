package com.kvteam.deliverytracker.performerapp.ui.main.userslist

import android.os.Bundle
import com.kvteam.deliverytracker.core.async.invokeAsync
import com.kvteam.deliverytracker.core.instance.IInstanceManager
import com.kvteam.deliverytracker.core.models.UserModel
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_users_list.*
import javax.inject.Inject

class ManagersListFragment: UsersListFragment() {
    @Inject
    lateinit var instanceManager: IInstanceManager

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidSupportInjection.inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if(savedInstanceState != null) {
            return
        }
        invokeAsync({
            //instanceManager.getManagers()
            var cnt = 0
            listOf(
                    UserModel(username = cnt.toString(), surname= cnt.toString(), name = cnt.toString(), phoneNumber = (cnt++).toString() ),
                    UserModel(username = cnt.toString(), surname= cnt.toString(), name = cnt.toString(), phoneNumber = (cnt++).toString() ),
                    UserModel(username = cnt.toString(), surname= cnt.toString(), name = cnt.toString(), phoneNumber = (cnt++).toString() ),
                    UserModel(username = cnt.toString(), surname= cnt.toString(), name = cnt.toString(), phoneNumber = (cnt++).toString() ),
                    UserModel(username = cnt.toString(), surname= cnt.toString(), name = cnt.toString(), phoneNumber = (cnt++).toString() ),
                    UserModel(username = cnt.toString(), surname= cnt.toString(), name = cnt.toString(), phoneNumber = (cnt++).toString() ),
                    UserModel(username = cnt.toString(), surname= cnt.toString(), name = cnt.toString(), phoneNumber = (cnt++).toString() ),
                    UserModel(username = cnt.toString(), surname= cnt.toString(), name = cnt.toString(), phoneNumber = (cnt++).toString() ),
                    UserModel(username = cnt.toString(), surname= cnt.toString(), name = cnt.toString(), phoneNumber = (cnt++).toString() ),
                    UserModel(username = cnt.toString(), surname= cnt.toString(), name = cnt.toString(), phoneNumber = (cnt++).toString() ),
                    UserModel(username = cnt.toString(), surname= cnt.toString(), name = cnt.toString(), phoneNumber = (cnt++).toString() ),
                    UserModel(username = cnt.toString(), surname= cnt.toString(), name = cnt.toString(), phoneNumber = (cnt++).toString() ),
                    UserModel(username = cnt.toString(), surname= cnt.toString(), name = cnt.toString(), phoneNumber = (cnt++).toString() ),
                    UserModel(username = cnt.toString(), surname= cnt.toString(), name = cnt.toString(), phoneNumber = (cnt++).toString() ),
                    UserModel(username = cnt.toString(), surname= cnt.toString(), name = cnt.toString(), phoneNumber = (cnt++).toString() ),
                    UserModel(username = cnt.toString(), surname= cnt.toString(), name = cnt.toString(), phoneNumber = (cnt++).toString() ),
                    UserModel(username = cnt.toString(), surname= cnt.toString(), name = cnt.toString(), phoneNumber = (cnt++).toString() ),
                    UserModel(username = cnt.toString(), surname= cnt.toString(), name = cnt.toString(), phoneNumber = (cnt++).toString() ),
                    UserModel(username = cnt.toString(), surname= cnt.toString(), name = cnt.toString(), phoneNumber = (cnt++).toString() ),
                    UserModel(username = cnt.toString(), surname= cnt.toString(), name = cnt.toString(), phoneNumber = (cnt++).toString() ),
                    UserModel(username = cnt.toString(), surname= cnt.toString(), name = cnt.toString(), phoneNumber = (cnt++).toString() ),
                    UserModel(username = cnt.toString(), surname= cnt.toString(), name = cnt.toString(), phoneNumber = (cnt++).toString() ),
                    UserModel(username = cnt.toString(), surname= cnt.toString(), name = cnt.toString(), phoneNumber = (cnt++).toString() ),
                    UserModel(username = cnt.toString(), surname= cnt.toString(), name = cnt.toString(), phoneNumber = (cnt++).toString() ),
                    UserModel(username = cnt.toString(), surname= cnt.toString(), name = cnt.toString(), phoneNumber = (cnt++).toString() ),
                    UserModel(username = cnt.toString(), surname= cnt.toString(), name = cnt.toString(), phoneNumber = (cnt++).toString() ),
                    UserModel(username = cnt.toString(), surname= cnt.toString(), name = cnt.toString(), phoneNumber = (cnt++).toString() ),
                    UserModel(username = cnt.toString(), surname= cnt.toString(), name = cnt.toString(), phoneNumber = (cnt++).toString() ),
                    UserModel(username = cnt.toString(), surname= cnt.toString(), name = cnt.toString(), phoneNumber = (cnt++).toString() ),
                    UserModel(username = cnt.toString(), surname= cnt.toString(), name = cnt.toString(), phoneNumber = (cnt++).toString() ),
                    UserModel(username = cnt.toString(), surname= cnt.toString(), name = cnt.toString(), phoneNumber = (cnt++).toString() ),
                    UserModel(username = cnt.toString(), surname= cnt.toString(), name = cnt.toString(), phoneNumber = (cnt++).toString() ),
                    UserModel(username = cnt.toString(), surname= cnt.toString(), name = cnt.toString(), phoneNumber = (cnt++).toString() ),
                    UserModel(username = cnt.toString(), surname= cnt.toString(), name = cnt.toString(), phoneNumber = (cnt++).toString() ),
                    UserModel(username = cnt.toString(), surname= cnt.toString(), name = cnt.toString(), phoneNumber = (cnt++).toString() ),
                    UserModel(username = cnt.toString(), surname= cnt.toString(), name = cnt.toString(), phoneNumber = (cnt++).toString() ),
                    UserModel(username = cnt.toString(), surname= cnt.toString(), name = cnt.toString(), phoneNumber = (cnt++).toString() ),
                    UserModel(username = cnt.toString(), surname= cnt.toString(), name = cnt.toString(), phoneNumber = (cnt++).toString() ),
                    UserModel(username = cnt.toString(), surname= cnt.toString(), name = cnt.toString(), phoneNumber = (cnt++).toString() ),
                    UserModel(username = cnt.toString(), surname= cnt.toString(), name = cnt.toString(), phoneNumber = (cnt++).toString() ),
                    UserModel(username = cnt.toString(), surname= cnt.toString(), name = cnt.toString(), phoneNumber = (cnt++).toString() ),
                    UserModel(username = cnt.toString(), surname= cnt.toString(), name = cnt.toString(), phoneNumber = (cnt++).toString() )
            )
        }, {
            if(it != null) {
                adapter.value?.items?.addAll(it)
                adapter.value?.notifyDataSetChanged()
            }
        })
    }
}