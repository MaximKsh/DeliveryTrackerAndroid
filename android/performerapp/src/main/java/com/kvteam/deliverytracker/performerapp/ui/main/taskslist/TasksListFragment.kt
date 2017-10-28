package com.kvteam.deliverytracker.performerapp.ui.main.taskslist


import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kvteam.deliverytracker.core.ui.AutoClearedValue
import com.kvteam.deliverytracker.core.ui.DeliveryTrackerFragment
import com.kvteam.deliverytracker.core.ui.DeliveryTrackerViewModelFactory

import com.kvteam.deliverytracker.performerapp.R
import com.kvteam.deliverytracker.performerapp.databinding.FragmentTasksListBinding
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject


open class TasksListFragment : DeliveryTrackerFragment() {
    @Inject
    lateinit var vmFactory: DeliveryTrackerViewModelFactory

    lateinit var divider: DividerItemDecoration

    protected lateinit var binding: AutoClearedValue<FragmentTasksListBinding>
    protected lateinit var adapter: AutoClearedValue<TasksListAdapter>

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidSupportInjection.inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val dataBinding = DataBindingUtil.inflate<FragmentTasksListBinding>(
                inflater,
                R.layout.fragment_tasks_list,
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
        this.divider = DividerItemDecoration(this.context, DividerItemDecoration.VERTICAL)

        val viewModel = ViewModelProviders
                .of(this, vmFactory)
                .get(TasksListViewModel::class.java)

        this.adapter = AutoClearedValue(
                this,
                TasksListAdapter())

        binding.value?.viewModel = viewModel
        binding.value?.fragment = this
        binding.value?.tasksList?.adapter = this.adapter.value
    }

}
