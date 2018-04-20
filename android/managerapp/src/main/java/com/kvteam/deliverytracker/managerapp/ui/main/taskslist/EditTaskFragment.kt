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
import android.widget.TextView
import com.basgeekball.awesomevalidation.AwesomeValidation
import com.basgeekball.awesomevalidation.ValidationStyle
import com.kvteam.deliverytracker.core.async.launchUI
import com.kvteam.deliverytracker.core.common.ILocalizationManager
import com.kvteam.deliverytracker.core.dataprovider.base.DataProvider
import com.kvteam.deliverytracker.core.dataprovider.base.DataProviderGetMode
import com.kvteam.deliverytracker.core.dataprovider.base.NetworkException
import com.kvteam.deliverytracker.core.session.ISession
import com.kvteam.deliverytracker.core.ui.DeliveryTrackerActivity
import com.kvteam.deliverytracker.core.ui.DeliveryTrackerFragment
import com.kvteam.deliverytracker.core.ui.errorhandling.IErrorHandler
import com.kvteam.deliverytracker.core.ui.toolbar.ToolbarController
import com.kvteam.deliverytracker.managerapp.R
import com.kvteam.deliverytracker.managerapp.ui.main.NavigationController
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_edit_task.*
import java.lang.ref.WeakReference
import java.util.*
import javax.inject.Inject

private const val stepperHeight = 195
private const val NUM_PAGES = 5

class EditTaskFragment : DeliveryTrackerFragment() {
    class TaskViewPageListener(dtActivity: DeliveryTrackerActivity) : ViewPager.OnPageChangeListener {
        private val activityWeak = WeakReference(dtActivity)

        override fun onPageScrollStateChanged(state: Int) {}

        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

        override fun onPageSelected(position: Int) {
            val dtActivity = activityWeak.get() ?: return
            val tvPrev = dtActivity.findViewById<TextView>(R.id.tvPrev) ?: return
            val tvNext = dtActivity.findViewById<TextView>(R.id.tvNext) ?: return

            dtActivity.softKeyboard.initEditTexts()
            when (position) {
                0 -> tvPrev.setTextColor(Color.LTGRAY)
                NUM_PAGES - 1 -> tvNext.setTextColor(Color.LTGRAY)
                else -> {
                    tvPrev.setTextColor(Color.GRAY)
                    tvNext.setTextColor(ContextCompat.getColor(dtActivity.baseContext, R.color.colorPrimary))
                }
            }
        }
    }

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

    private lateinit var mPagerAdapter: PagerAdapter

    private var originalStepperHeight: Int = 0

    val fragments = listOf(
            TaskNumberAndDetailsFragment(),
            TaskDeliveryDateFragment(),
            TaskReceiptAtFragment(),
            TaskProductsFragment(),
            TaskClientFragment()
    )

    private var offsetScroll = 0

    private val taskIdKey = "task"
    private var taskId
        get() = arguments?.getSerializable(taskIdKey)!! as UUID
        set(value) = arguments?.putSerializable(taskIdKey, value)!!

    private val tryPrefetchKey = "tryPrefetch"
    private var tryPrefetch: Boolean
        get() = arguments?.getBoolean(tryPrefetchKey) ?: false
        set(value) = arguments?.putBoolean(tryPrefetchKey, value)!!



    override fun configureToolbar(toolbar: ToolbarController) {
        toolbar.disableDropDown()
        toolbar.setToolbarTitle("Task")
        toolbar.showBackButton()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidSupportInjection.inject(this)
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_edit_task, container, false)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) = launchUI {
        super.onActivityCreated(savedInstanceState)

        dtActivity.addOnKeyboardShowListener(::hideStepper)

        dtActivity.addOnKeyboardHideListener(::showStepper)

        mPagerAdapter = ScreenSlidePagerAdapter(childFragmentManager)
        pager.adapter = mPagerAdapter
        indicator.setViewPager(pager)

        originalStepperHeight = rlStepperContainer.layoutParams.height
        tvPrev.setTextColor(Color.LTGRAY)

        pager.addOnPageChangeListener(TaskViewPageListener(this@EditTaskFragment.dtActivity))

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

    override fun onStop() {
        val selectedItem = pager?.currentItem
        if (selectedItem != null && selectedItem in 1..NUM_PAGES) {
            val fragment = fragments[selectedItem]
            if (fragment.shouldDeleteDirty()) {
                dp.taskInfos.invalidateDirty(taskId)
            }
        }
        super.onStop()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = launchUI({
        when (item.itemId) {
            R.id.action_done -> {
                val (task, _) = dp.taskInfos.get(taskId, DataProviderGetMode.DIRTY)
                if (task.taskNumber?.isNotBlank() != true) {
                    pager.currentItem = 0
                    val validation = AwesomeValidation(ValidationStyle.UNDERLABEL)
                    validation.addValidation(
                            this@EditTaskFragment.dtActivity,
                            R.id.etTaskNumber,
                            {it.isNotBlank()},
                            com.kvteam.deliverytracker.core.R.string.Core_TaskNumberValidationError)
                    validation.setContext(this@EditTaskFragment.dtActivity)
                    validation.validate()
                    return@launchUI
                }

                task.instanceId = session.instance?.id

                try {
                    val clientId = task.clientId
                    if (clientId != null) {
                        val oldClient = dp.clients.get(clientId, DataProviderGetMode.DIRTY).entry
                        dp.clients.upsertAsync(oldClient)
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

    override fun onDestroyView() {
        dtActivity.removeKeyboardListener(::showStepper)
        dtActivity.removeKeyboardListener(::hideStepper)
        super.onDestroyView()
    }


    private fun showStepper () {
        if (rlStepperContainer.height != 0) {
            return
        }
        rlStepperContainer.measure(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT)
        val height = rlStepperContainer.measuredHeight
        val anim = ValueAnimator.ofInt(0, height)
        anim.addUpdateListener { valueAnimator ->
            val value = valueAnimator.animatedValue as Int
            val layoutParams = rlStepperContainer.layoutParams
            layoutParams.height = value
            rlStepperContainer.layoutParams = layoutParams
        }
        anim.duration = 75L
        anim.start()
        val currentFragmentScrollView = fragments[pager.currentItem].view
        if (currentFragmentScrollView != null) {
            currentFragmentScrollView.scrollY = currentFragmentScrollView.scrollY - offsetScroll
        }

    }

    private fun hideStepper () {
        if (rlStepperContainer.height == 0) {
            return
        }

        val anim = ValueAnimator.ofInt(rlStepperContainer.height, 0)
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
        if (currentFragmentScrollView != null) {
            currentFragmentScrollView.scrollY = currentFragmentScrollView.scrollY + offsetScroll
        }
    }

    fun setTask(id: UUID?) {
        this.taskId = id ?: UUID.randomUUID()
        this.tryPrefetch = id != null
    }
}

