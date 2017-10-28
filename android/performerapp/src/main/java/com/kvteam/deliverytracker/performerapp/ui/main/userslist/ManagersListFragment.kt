package com.kvteam.deliverytracker.performerapp.ui.main.userslist

import android.os.Bundle
import com.kvteam.deliverytracker.core.async.invokeAsync
import com.kvteam.deliverytracker.core.instance.IInstanceManager
import dagger.android.support.AndroidSupportInjection
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
        binding.value?.viewModel?.header?.set("Managers")
        invokeAsync({
            instanceManager.getManagers()
        }, {
            adapter.value?.replace(it)
        })
    }
}