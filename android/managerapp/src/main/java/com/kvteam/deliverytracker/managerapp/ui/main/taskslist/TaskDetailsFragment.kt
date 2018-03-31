package com.kvteam.deliverytracker.managerapp.ui.main.taskslist

import com.kvteam.deliverytracker.core.ui.tasks.BaseTaskDetailsFragment
import com.kvteam.deliverytracker.managerapp.ui.main.NavigationController
import javax.inject.Inject
import android.view.animation.Animation
import android.view.animation.Animation.AnimationListener
import android.view.animation.AnimationUtils
import android.os.Build
import android.view.View


class TaskDetailsFragment : BaseTaskDetailsFragment() {
    @Inject
    lateinit var navigationController: NavigationController

    override fun closeFragment() {
        navigationController.closeCurrentFragment()
    }

}