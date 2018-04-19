package com.kvteam.deliverytracker.managerapp.ui.main.taskslist

import android.animation.ValueAnimator
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.content.ContextCompat
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.view.*
import com.basgeekball.awesomevalidation.AwesomeValidation
import com.basgeekball.awesomevalidation.ValidationStyle
import com.kvteam.deliverytracker.core.async.launchUI
import com.kvteam.deliverytracker.core.common.ILocalizationManager
import com.kvteam.deliverytracker.core.dataprovider.base.DataProvider
import com.kvteam.deliverytracker.core.dataprovider.base.DataProviderGetMode
import com.kvteam.deliverytracker.core.dataprovider.base.NetworkException
import com.kvteam.deliverytracker.core.session.ISession
import com.kvteam.deliverytracker.core.ui.DeliveryTrackerFragment
import com.kvteam.deliverytracker.core.ui.errorhandling.IErrorHandler
import com.kvteam.deliverytracker.core.ui.toolbar.ToolbarController
import com.kvteam.deliverytracker.managerapp.R
import com.kvteam.deliverytracker.managerapp.ui.main.MainActivity
import com.kvteam.deliverytracker.managerapp.ui.main.NavigationController
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_edit_task.*
import java.util.*
import javax.inject.Inject

const val stepperHeight = 195

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
    lateinit var session: ISession

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

    private var originalStepperHeight: Int = 0

    val fragments = listOf(
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

    private val modeKey = "task"
    private var displayMode
        get() = arguments?.getString(modeKey)!!
        set(value) = arguments?.putString(modeKey, value)!!

    private val tryPrefetchKey = "tryPrefetch"
    private var tryPrefetch: Boolean
        get() = arguments?.getBoolean(tryPrefetchKey) ?: false
        set(value) = arguments?.putBoolean(tryPrefetchKey, value)!!

    fun setMode (mode: String) {
        displayMode = mode
    }

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

    private var offsetScroll = 0

    private fun showStepper () {
        if (rlStepperContainer.height == 0) {
            val anim = ValueAnimator.ofInt(0, originalStepperHeight)
            anim.addUpdateListener { valueAnimator ->
                val value = valueAnimator.animatedValue as Int
                val layoutParams = rlStepperContainer.layoutParams
                layoutParams.height = value
                rlStepperContainer.layoutParams = layoutParams
            }
            anim.duration = 75L
            anim.start()
            val currentFragmentScrollView = fragments[pager.currentItem].view
            currentFragmentScrollView!!.scrollY = currentFragmentScrollView.scrollY - offsetScroll
        }
    }

    override fun onDestroyView() {
        (activity as MainActivity).removeKeyboardListener(::showStepper)

        (activity as MainActivity).removeKeyboardListener(::hideStepper)
        super.onDestroyView()
    }

    private fun hideStepper () {
        val anim = ValueAnimator.ofInt(originalStepperHeight, 0)
        anim.addUpdateListener { valueAnimator ->
            val value = valueAnimator.animatedValue as Int
            val layoutParams = rlStepperContainer.layoutParams
            layoutParams.height = value
            rlStepperContainer.layoutParams = layoutParams
        }
        anim.duration = 75L
        anim.start()
        offsetScroll = stepperHeight
        val currentFragmentScrollView = fragments[pager.currentItem].view
        currentFragmentScrollView!!.scrollY = currentFragmentScrollView.scrollY + offsetScroll
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) = launchUI {
        super.onActivityCreated(savedInstanceState)

        (activity as MainActivity).addOnKeyboardShowListener(::hideStepper)

        (activity as MainActivity).addOnKeyboardHideListener(::showStepper)

        mPagerAdapter = ScreenSlidePagerAdapter(childFragmentManager)
        pager.adapter = mPagerAdapter
        indicator.setViewPager(pager)

        originalStepperHeight = rlStepperContainer.layoutParams.height
        tvPrev.setTextColor(Color.LTGRAY)

        pager.addOnPageChangeListener(object: ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {}

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

            override fun onPageSelected(position: Int) {
                (activity as MainActivity).softKeyboard.initEditTexts()
                when (position) {
                    0 -> tvPrev.setTextColor(Color.LTGRAY)
                    NUM_PAGES - 1 -> tvNext.setTextColor(Color.LTGRAY)
                    else -> {
                        tvPrev.setTextColor(Color.GRAY)
                        tvNext.setTextColor(ContextCompat.getColor(activity!!.baseContext, R.color.colorPrimary))
                    }
                }
            }

        })

        tvPrev.setOnClickListener { _ ->
            if (pager.currentItem > 0) {
                pager.currentItem = pager.currentItem - 1
            }
        }
        tvNext.setOnClickListener { _ ->
            if (pager.currentItem <= NUM_PAGES) {
                pager.currentItem = pager.currentItem + 1
            }
        }
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

                val (task, _) = dp.taskInfos.get(taskId, DataProviderGetMode.DIRTY)
                task.instanceId = session.instance?.id

                try {
                    // TODO address
                    /*if (task.clientId != null) {
                        val oldClient = dp.clients.get(task.clientId as UUID, DataProviderGetMode.DIRTY).entry
                        val selectedAddress = oldClient.clientAddresses.find{ it.id == task.clientAddressId}
                        val newClient = dp.clients.upsertAsync(dp.clients.get(task.clientId as UUID, DataProviderGetMode.DIRTY).entry)
                        task.clientId = newClient.id
                        val newAddress = newClient.clientAddresses.find { it.rawAddress == selectedAddress?.rawAddress }
                        task.clientAddressId = newAddress?.id
                    }*/

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

