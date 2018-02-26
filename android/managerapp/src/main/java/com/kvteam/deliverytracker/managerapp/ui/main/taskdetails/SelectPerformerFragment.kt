package com.kvteam.deliverytracker.managerapp.ui.main.taskdetails

import android.location.Geocoder
import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kvteam.deliverytracker.core.common.ILocalizationManager
import com.kvteam.deliverytracker.core.models.User
import com.kvteam.deliverytracker.core.ui.AutoClearedValue
import com.kvteam.deliverytracker.core.ui.DeliveryTrackerFragment
import com.kvteam.deliverytracker.managerapp.R
import com.kvteam.deliverytracker.managerapp.ui.main.NavigationController
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_select_performers_list.*
import java.util.*
import javax.inject.Inject


open class SelectPerformerFragment : DeliveryTrackerFragment() {
    protected val layoutManagerKey = "layoutManager"
    protected val performersListKey = "performersList"

    @Inject
    lateinit var navigationController: NavigationController

    //@Inject
    //lateinit var taskRepository: ITaskRepository


    @Inject
    lateinit var lm: ILocalizationManager

    protected lateinit var adapter: AutoClearedValue<SelectPerformerAdapter>

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidSupportInjection.inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?): View? {
        return view ?: inflater
                .inflate(R.layout.fragment_select_performers_list, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        rvAvailablePerformersList.layoutManager = LinearLayoutManager(
                activity!!.applicationContext,
                LinearLayoutManager.VERTICAL,
                false)
        rvAvailablePerformersList.addItemDecoration(
                DividerItemDecoration(context, DividerItemDecoration.VERTICAL))

        val geocoder = Geocoder(this.context, Locale.getDefault())
        adapter = AutoClearedValue(
                this,
                SelectPerformerAdapter(geocoder, lm, this::onClick),
                {
                    it?.geocoder = null
                    it?.lm = null
                    it?.onClick = null
                })
        rvAvailablePerformersList.adapter = adapter.value

        if(savedInstanceState == null) {
            /*invokeAsync({
                taskRepository.getAvailablePerformers()
            }, {
                if(it.success) {
                    adapter.value?.items?.addAll(it.entity!!)
                    adapter.value?.notifyDataSetChanged()
                }
            })*/
        } else {
            savedInstanceState.apply {
                val adapter = adapter.value
                val layoutManager = rvAvailablePerformersList?.layoutManager
                if(adapter != null
                        && layoutManager != null) {
                    if(containsKey(performersListKey)
                            && containsKey(layoutManagerKey)) {
                        val savedUsers = getParcelableArray(performersListKey).map { it as User }
                        adapter.items.clear()
                        adapter.items.addAll(savedUsers)
                        layoutManager.onRestoreInstanceState(getParcelable(layoutManagerKey))
                    }
                }
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.apply {
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

    private fun onClick(user: User) {
        if(navigationController.info.containsKey(SELECTED_USERS_KEY)) {
            navigationController.info.remove(SELECTED_USERS_KEY)
        }

        navigationController.info.put(SELECTED_USERS_KEY, user)
        navigationController.closeCurrentFragment()
    }
}
