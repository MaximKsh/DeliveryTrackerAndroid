package com.kvteam.deliverytracker.managerapp.ui.main.addtask

import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kvteam.deliverytracker.core.async.invokeAsync
import com.kvteam.deliverytracker.core.models.UserModel
import com.kvteam.deliverytracker.core.ui.AutoClearedValue
import com.kvteam.deliverytracker.core.ui.DeliveryTrackerFragment
import com.kvteam.deliverytracker.managerapp.R
import com.kvteam.deliverytracker.managerapp.tasks.ITaskRepository
import com.kvteam.deliverytracker.managerapp.ui.main.NavigationController
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_select_performers_list.*
import javax.inject.Inject


open class SelectPerformerFragment : DeliveryTrackerFragment() {
    protected val layoutManagerKey = "layoutManager"
    protected val performersListKey = "performersList"

    @Inject
    lateinit var navigationController: NavigationController

    @Inject
    lateinit var taskRepository: ITaskRepository

    protected lateinit var adapter: AutoClearedValue<SelectPerformerAdapter>

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidSupportInjection.inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
            inflater: LayoutInflater?,
            container: ViewGroup?,
            savedInstanceState: Bundle?): View? {
        return view ?: inflater
                ?.inflate(R.layout.fragment_select_performers_list, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        rvAvailablePerformersList.layoutManager = LinearLayoutManager(
                activity.applicationContext,
                LinearLayoutManager.VERTICAL,
                false)
        rvAvailablePerformersList.addItemDecoration(
                DividerItemDecoration(context, DividerItemDecoration.VERTICAL))

        adapter = AutoClearedValue(
                this,
                SelectPerformerAdapter(this::onClick),
                {
                    it?.onClick = null
                })
        rvAvailablePerformersList.adapter = adapter.value

        if(savedInstanceState == null) {
            invokeAsync({
                taskRepository.getAvailablePerformers()
            }, {
                if(it != null) {
                    adapter.value?.items?.addAll(it)
                    adapter.value?.notifyDataSetChanged()
                }
            })
        } else {
            savedInstanceState.apply {
                val adapter = adapter.value
                val layoutManager = rvAvailablePerformersList?.layoutManager
                if(adapter != null
                        && layoutManager != null) {
                    if(containsKey(performersListKey)
                            && containsKey(layoutManagerKey)) {
                        val savedUsers = getParcelableArray(performersListKey).map { it as UserModel }
                        adapter.items.clear()
                        adapter.items.addAll(savedUsers)
                        layoutManager.onRestoreInstanceState(getParcelable(layoutManagerKey))
                    }
                }
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.apply {
            val adapter = adapter.value
            val layoutManager = rvAvailablePerformersList?.layoutManager
            if(adapter != null
                    && layoutManager != null) {
                putParcelableArray(
                        performersListKey,
                        adapter.items.toTypedArray())
                putParcelable(
                        layoutManagerKey,
                        layoutManager.onSaveInstanceState())
            }

        }
    }

    private fun onClick(user: UserModel) {
        if(navigationController.info.containsKey(SELECTED_USERS_KEY)) {
            navigationController.info.remove(SELECTED_USERS_KEY)
        }

        navigationController.info.put(SELECTED_USERS_KEY, user)
        navigationController.closeCurrentFragment()
    }
}