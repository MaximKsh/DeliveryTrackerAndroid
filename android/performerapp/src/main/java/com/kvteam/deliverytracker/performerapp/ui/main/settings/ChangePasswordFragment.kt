package com.kvteam.deliverytracker.performerapp.ui.main.settings

import com.kvteam.deliverytracker.core.ui.settings.BaseChangePasswordFragment
import com.kvteam.deliverytracker.performerapp.ui.main.NavigationController
import javax.inject.Inject

class ChangePasswordFragment : BaseChangePasswordFragment() {
    @Inject
    lateinit var navigationController: NavigationController


    override fun onSuccessChangePassword() {
        navigationController.closeCurrentFragment()
    }
}