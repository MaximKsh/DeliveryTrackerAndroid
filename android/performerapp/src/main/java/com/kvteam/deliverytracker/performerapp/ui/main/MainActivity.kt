package com.kvteam.deliverytracker.performerapp.ui.main

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.databinding.Observable
import android.os.Bundle
import android.view.MenuItem
import com.kvteam.deliverytracker.core.ui.AutoClearedValue
import com.kvteam.deliverytracker.core.ui.DeliveryTrackerActivity
import com.kvteam.deliverytracker.core.ui.DeliveryTrackerViewModelFactory
import com.kvteam.deliverytracker.performerapp.R
import com.kvteam.deliverytracker.performerapp.databinding.ActivityMainBinding
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

class MainActivity : DeliveryTrackerActivity() {
    private val managersMenu = R.id.navigation_managers
    private val performersMenu = R.id.navigation_performers
    private val myTasksMenu = R.id.navigation_my_tasks
    private val undistributedTasksMenu = R.id.navigation_undistributed_tasks

    private val menuItemMapper = mapOf(
            managersMenu to {navigationController.navigateToManagers()},
            performersMenu to {navigationController.navigateToPerformers()},
            myTasksMenu to {navigationController.navigateToMyTasks()},
            undistributedTasksMenu to {navigationController.navigateToUndistributedTasks()}
    )

    @Inject
    lateinit var vmFactory: DeliveryTrackerViewModelFactory

    @Inject
    lateinit var navigationController: NavigationController

    private lateinit var binding: AutoClearedValue<ActivityMainBinding>

    override val checkHasAccountOnResume
            get() = true

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)

        val binding = DataBindingUtil
                .setContentView<ActivityMainBinding>(
                        this,
                        com.kvteam.deliverytracker.performerapp.R.layout.activity_main)
        this.binding = AutoClearedValue(
                this,
                binding,
                {
                    it?.executePendingBindings()
                    it?.unbind()
                    it?.activity = null
                    it?.viewModel = null
                })
        val viewModel = ViewModelProviders
                .of(this, vmFactory)
                .get(MainViewModel::class.java)
        this.binding.value?.viewModel = viewModel
        this.binding.value?.activity = this

        if (savedInstanceState == null) {
            viewModel.selectedBottomMenu.value = performersMenu
        }
        viewModel.selectedBottomMenu.observe(this, Observer{ menuItemMapper[it]?.invoke() })
    }
}
