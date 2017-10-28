package com.kvteam.deliverytracker.performerapp.ui.main.task


import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kvteam.deliverytracker.core.ui.AutoClearedValue
import com.kvteam.deliverytracker.core.ui.DeliveryTrackerViewModelFactory

import com.kvteam.deliverytracker.performerapp.R
import com.kvteam.deliverytracker.performerapp.databinding.FragmentTaskBinding
import com.kvteam.deliverytracker.performerapp.ui.main.NavigationController
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject


class TaskFragment : Fragment() {
    @Inject
    lateinit var vmFactory: DeliveryTrackerViewModelFactory
    @Inject
    lateinit var navigation: NavigationController

    private lateinit var binding: AutoClearedValue<FragmentTaskBinding>

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidSupportInjection.inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val dataBinding = DataBindingUtil.inflate<FragmentTaskBinding>(
                inflater,
                R.layout.fragment_task,
                container,
                false)
        binding = AutoClearedValue(
                this,
                dataBinding,
                {
                    it?.executePendingBindings()
                    it?.unbind()
                    it?.fragment = null
                    it?.viewModel = null
                })
        return dataBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val viewModel = ViewModelProviders
                .of(this, vmFactory)
                .get(TaskViewModel::class.java)

        binding.value?.viewModel = viewModel
        binding.value?.fragment = this

        viewModel.number.set("123")
    }
}
