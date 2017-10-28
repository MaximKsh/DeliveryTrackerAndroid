package com.kvteam.deliverytracker.performerapp.ui.main.performerslist


import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DividerItemDecoration
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kvteam.deliverytracker.core.async.invokeAsync
import com.kvteam.deliverytracker.core.instance.IInstanceManager
import com.kvteam.deliverytracker.core.ui.AutoClearedValue
import com.kvteam.deliverytracker.core.ui.DeliveryTrackerViewModelFactory

import com.kvteam.deliverytracker.performerapp.R
import com.kvteam.deliverytracker.performerapp.databinding.FragmentPerformersListBinding
import com.kvteam.deliverytracker.performerapp.ui.main.NavigationController
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject


class PerformersListFragment : Fragment() {
    @Inject
    lateinit var instanceManager: IInstanceManager
    @Inject
    lateinit var vmFactory: DeliveryTrackerViewModelFactory
    @Inject
    lateinit var navigation: NavigationController

    lateinit var divider: DividerItemDecoration

    private lateinit var binding: AutoClearedValue<FragmentPerformersListBinding>
    private lateinit var adapter: AutoClearedValue<PerformersListAdapter>

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidSupportInjection.inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val dataBinding = DataBindingUtil.inflate<FragmentPerformersListBinding>(
                inflater,
                R.layout.fragment_performers_list,
                container,
                false)
        binding = AutoClearedValue(this, dataBinding)
        return dataBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        this.divider = DividerItemDecoration(this.context, DividerItemDecoration.VERTICAL)

        val viewModel = ViewModelProviders
                .of(this, vmFactory)
                .get(PerformersListViewModel::class.java)

        this.adapter = AutoClearedValue(this, PerformersListAdapter())

        binding.value?.viewModel = viewModel
        binding.value?.fragment = this
        binding.value?.performersList?.adapter = this.adapter.value

        invokeAsync({
            instanceManager.getPerformers()
        }, {
            val a = it
            adapter.value?.replace(a)
        })
    }



}