package com.kvteam.deliverytracker.managerapp.ui.main.taskslist

import android.animation.ValueAnimator
import android.app.*
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.*
import com.amulyakhare.textdrawable.TextDrawable
import com.kvteam.deliverytracker.core.async.launchUI
import com.kvteam.deliverytracker.core.common.ILocalizationManager
import com.kvteam.deliverytracker.core.dataprovider.CacheException
import com.kvteam.deliverytracker.core.dataprovider.DataProvider
import com.kvteam.deliverytracker.core.dataprovider.DataProviderGetMode
import com.kvteam.deliverytracker.core.dataprovider.NetworkException
import com.kvteam.deliverytracker.core.models.PaymentType
import com.kvteam.deliverytracker.core.models.Product
import com.kvteam.deliverytracker.core.ui.DeliveryTrackerFragment
import com.kvteam.deliverytracker.core.ui.autocomplete.AutocompleteListAdapter
import com.kvteam.deliverytracker.core.ui.autocomplete.ClientsAutoCompleteAdapter
import com.kvteam.deliverytracker.core.ui.dropdownselect.DropdownSelect
import com.kvteam.deliverytracker.core.ui.dropdownselect.DropdownSelectItem
import com.kvteam.deliverytracker.core.ui.errorhandling.IErrorHandler
import com.kvteam.deliverytracker.core.ui.toolbar.ToolbarController
import com.kvteam.deliverytracker.core.webservice.ITaskWebservice
import com.kvteam.deliverytracker.core.webservice.IViewWebservice
import com.kvteam.deliverytracker.managerapp.R
import com.kvteam.deliverytracker.managerapp.ui.main.NavigationController
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.client_info.*
import kotlinx.android.synthetic.main.fragment_edit_task.*
import kotlinx.android.synthetic.main.fragment_edit_task.view.*
import kotlinx.android.synthetic.main.selected_performer_item.*
import kotlinx.android.synthetic.main.selected_performer_item.view.*
import kotlinx.android.synthetic.main.selected_product_item.*
import kotlinx.android.synthetic.main.selected_product_item.view.*
import kotlinx.coroutines.experimental.runBlocking
import org.joda.time.DateTime
import java.util.*
import javax.inject.Inject
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager

class EditTaskFragment : DeliveryTrackerFragment() {
    @Inject
    lateinit var navigationController: NavigationController

    @Inject
    lateinit var eh: IErrorHandler

    @Inject
    lateinit var lm: ILocalizationManager

    @Inject
    lateinit var dp: DataProvider

    private val pageIndexKey = "pageIndex"
    private var pageIndex
        get() = arguments?.getInt(pageIndexKey)!!
        set(value) = arguments?.putInt(pageIndexKey, value)!!

    private val NUM_PAGES = 7

    private lateinit var mPagerAdapter: PagerAdapter

    private inner class ScreenSlidePagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {
        override fun getPageTitle(position: Int): CharSequence? {
            return (position + 1).toString()
        }

        override fun getItem(position: Int): Fragment {
            return when (position) {
                0 -> {
                    val fragment = TaskNumberAndDetailsFragment()
                    fragment
                }
                1 -> {
                    val fragment = TaskDeliveryDateFragment()
                    fragment.setTask(taskId)
                    fragment
                }
                2 -> {
                    val fragment = TaskReceiptAtFragment()
                    fragment.setTask(taskId)
                    fragment
                }
                3 -> {
                    val fragment = TaskPaymentTypeFragment()
                    fragment.setTask(taskId)
                    fragment
                }
                4 -> {
                    val fragment = TaskPerformerFragment()
                    fragment.setTask(taskId)
                    fragment
                }
                5 -> {
                    val fragment = TaskProductsFragment()
                    fragment.setTask(taskId)
                    fragment
                }
                6 -> {
                    val fragment = TaskClientFragment()
                    fragment.setTask(taskId)
                    fragment
                }
                else -> TaskNumberAndDetailsFragment()
            }
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
//        pager.currentItem = pageIndex
//        pager.addOnPageChangeListener(object: ViewPager.OnPageChangeListener {
//            override fun onPageScrollStateChanged(state: Int) {}
//
//            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
//
//            override fun onPageSelected(position: Int) {
//                pageIndex = position
//            }
//
//        })
        indicator.setViewPager(pager)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_edit_task, container, false)
        return view
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = launchUI({
        when (item.itemId) {
            R.id.action_add -> {
                val (task, _) = dp.taskInfos.getAsync(taskId, DataProviderGetMode.DIRTY)
//                task.taskNumber = etTaskNumber.text.toString()
                try {
                    if (task.clientId != null) {
                        dp.clients.upsertAsync(dp.clients.get(task.clientId as UUID, DataProviderGetMode.DIRTY).entry)
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
        inflater.inflate(R.menu.toolbar_edit_task_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }
}

