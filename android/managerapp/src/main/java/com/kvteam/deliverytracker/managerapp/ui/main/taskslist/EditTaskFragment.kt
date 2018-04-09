package com.kvteam.deliverytracker.managerapp.ui.main.taskslist

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.view.*
import com.basgeekball.awesomevalidation.AwesomeValidation
import com.basgeekball.awesomevalidation.ValidationStyle
import com.kvteam.deliverytracker.core.async.launchUI
import com.kvteam.deliverytracker.core.common.ILocalizationManager
import com.kvteam.deliverytracker.core.dataprovider.DataProvider
import com.kvteam.deliverytracker.core.dataprovider.DataProviderGetMode
import com.kvteam.deliverytracker.core.dataprovider.NetworkException
import com.kvteam.deliverytracker.core.ui.DeliveryTrackerFragment
import com.kvteam.deliverytracker.core.ui.errorhandling.IErrorHandler
import com.kvteam.deliverytracker.core.ui.toolbar.ToolbarController
import com.kvteam.deliverytracker.managerapp.R
import com.kvteam.deliverytracker.managerapp.ui.main.NavigationController
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_edit_task.*
import kotlinx.android.synthetic.main.fragment_task_number_and_details.*
import java.util.*
import javax.inject.Inject

abstract class PageFragment : DeliveryTrackerFragment() {
    private val taskIdKey = "task"
    protected var taskId
        get() = arguments?.getSerializable(taskIdKey)!! as UUID
        set(value) = arguments?.putSerializable(taskIdKey, value)!!

    fun setTask(id: UUID?) {
        this.taskId = id ?: UUID.randomUUID()
    }
}

class EditTaskFragment : DeliveryTrackerFragment() {
    @Inject
    lateinit var navigationController: NavigationController

    @Inject
    lateinit var eh: IErrorHandler

    @Inject
    lateinit var lm: ILocalizationManager

    @Inject
    lateinit var dp: DataProvider

    private val NUM_PAGES = 5

    private lateinit var mPagerAdapter: PagerAdapter

    private lateinit var validation: AwesomeValidation

    val fragments = listOf<PageFragment>(
            TaskNumberAndDetailsFragment(),
            TaskDeliveryDateFragment(),
            TaskReceiptAtFragment(),
            TaskProductsFragment(),
            TaskClientFragment()
    )

    private inner class ScreenSlidePagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {
        override fun getPageTitle(position: Int): CharSequence? {
            return (position + 1).toString()
        }

        override fun getItem(position: Int): Fragment {
            val fragment = fragments[position]
            fragment.setTask(taskId)
            return fragment
        }

        override fun getCount(): Int {
            return NUM_PAGES
        }
    }

    private val taskIdKey = "task"
    private var taskId
        get() = arguments?.getSerializable(taskIdKey)!! as UUID
        set(value) = arguments?.putSerializable(taskIdKey, value)!!

    private val tryPrefetchKey = "tryPrefetch"
    private var tryPrefetch: Boolean
        get() = arguments?.getBoolean(tryPrefetchKey) ?: false
        set(value) = arguments?.putBoolean(tryPrefetchKey, value)!!

    fun setTask(id: UUID?) {
        this.taskId = id ?: UUID.randomUUID()
        this.tryPrefetch = id != null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidSupportInjection.inject(this)
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
    }

    override fun configureToolbar(toolbar: ToolbarController) {
        toolbar.disableDropDown()
        toolbar.setToolbarTitle("Task")
        toolbar.showBackButton()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) = launchUI {
        super.onActivityCreated(savedInstanceState)

        mPagerAdapter = ScreenSlidePagerAdapter(childFragmentManager)
        pager.adapter = mPagerAdapter
        indicator.setViewPager(pager)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_edit_task, container, false)
        return view
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = launchUI({
        when (item.itemId) {
            R.id.action_done -> {
                val validation = AwesomeValidation(ValidationStyle.UNDERLABEL)
                validation.addValidation(
                        this@EditTaskFragment.dtActivity,
                        R.id.etTaskNumber,
                        {it.isNotBlank()},
                        com.kvteam.deliverytracker.core.R.string.Core_TaskNumberValidationError)
                validation.setContext(this@EditTaskFragment.dtActivity)
                if(!validation.validate()) {
                    return@launchUI
                }

                val (task, _) = dp.taskInfos.getAsync(taskId, DataProviderGetMode.DIRTY)
                try {
                    if (task.clientId != null) {
                        val oldClient = dp.clients.get(task.clientId as UUID, DataProviderGetMode.DIRTY).entry
                        val selectedAddress = oldClient.clientAddresses.find{ it.id == task.clientAddressId}
                        val newClient = dp.clients.upsertAsync(dp.clients.get(task.clientId as UUID, DataProviderGetMode.DIRTY).entry)
                        task.clientId = newClient.id
                        val newAddress = newClient.clientAddresses.find { it.rawAddress == selectedAddress?.rawAddress }
                        task.clientAddressId = newAddress?.id
                    }
                    dp.taskInfos.upsertAsync(task)
                } catch (e: NetworkException) {
                    eh.handle(e.result)
                }
                navigationController.closeCurrentFragment()
            }
        }
    }, {
        super.onOptionsItemSelected(item)
    })

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.done_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }
}

