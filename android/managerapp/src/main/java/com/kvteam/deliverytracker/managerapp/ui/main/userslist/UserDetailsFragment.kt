package com.kvteam.deliverytracker.managerapp.ui.main.userslist

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.PagerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kvteam.deliverytracker.core.async.launchUI
import com.kvteam.deliverytracker.core.common.ILocalizationManager
import com.kvteam.deliverytracker.core.dataprovider.base.DataProvider
import com.kvteam.deliverytracker.core.dataprovider.base.DataProviderGetMode
import com.kvteam.deliverytracker.core.roles.Role
import com.kvteam.deliverytracker.core.roles.toRole
import com.kvteam.deliverytracker.core.ui.DeliveryTrackerFragment
import com.kvteam.deliverytracker.core.ui.materialDefaultAvatar
import com.kvteam.deliverytracker.core.ui.toolbar.ToolbarController
import com.kvteam.deliverytracker.managerapp.R
import com.kvteam.deliverytracker.managerapp.ui.main.taskslist.UserTasksListFragment
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_user_details.*
import java.util.*
import javax.inject.Inject

class UserDetailsFragment : DeliveryTrackerFragment() {
    private inner class ScreenSlidePagerAdapter(fm: FragmentManager, val role: Role) : FragmentStatePagerAdapter(fm) {
        val mPageTitles = listOf("Tasks", "Statistics", "On map")

        override fun getPageTitle(position: Int): CharSequence? {
            return mPageTitles[position]
        }

        override fun getItem(position: Int): Fragment {
            val fragment = fragments[position]
            return fragment
        }

        override fun getCount(): Int {
            return if (role == Role.Creator || role == Role.Manager) NUM_PAGES - 1 else NUM_PAGES
        }
    }

    @Inject
    lateinit var lm: ILocalizationManager

    @Inject
    lateinit var dp: DataProvider

    private val NUM_PAGES = 3

    private lateinit var mPagerAdapter: PagerAdapter

    private lateinit var fragments : List<DeliveryTrackerFragment>

    private val userIdKey = "userId"
    private var userId
        get() = arguments?.getSerializable(userIdKey)!! as UUID
        set(value) = arguments?.putSerializable(userIdKey, value)!!

    fun setUser(id: UUID) {
        this.userId = id
    }



    override fun onActivityCreated(savedInstanceState: Bundle?) = launchUI {
        super.onActivityCreated(savedInstanceState)
        val user = dp.users.getAsync(userId, DataProviderGetMode.FORCE_WEB).entry
        tvUserRole.text = lm.getString(user.role!!.toRole()!!.localizationStringId)
        val materialAvatarDefault = materialDefaultAvatar(user)
        ivUserAvatar.setImageDrawable(materialAvatarDefault)
        tvUserName.text = "${user.name} ${user.surname}"
        ivOnlineStatus.visibility = if (user.online) View.VISIBLE else View.GONE
        tvUserPhoneNumber.text = user.phoneNumber
        slidingLayout.addPanelSlideListener(object: SlidingUpPanelLayout.PanelSlideListener {
            override fun onPanelSlide(panel: View?, slideOffset: Float) {}

            override fun onPanelStateChanged(panel: View?, previousState: SlidingUpPanelLayout.PanelState?, newState: SlidingUpPanelLayout.PanelState?) {
                when (newState) {
                    SlidingUpPanelLayout.PanelState.EXPANDED -> { toolbarController.setToolbarTitle("${user.name} ${user.surname}") }
                    SlidingUpPanelLayout.PanelState.COLLAPSED -> { toolbarController.setToolbarTitle("User")}
                    else -> {}
                }
            }
        })

        val userTasksFragment = UserTasksListFragment()
        userTasksFragment.setUser(user.id!!, user.role?.toRole()!!)
        fragments = listOf(
                userTasksFragment,
                UserStatsFragment(),
                UserOnMapFragment()
        )

        mPagerAdapter = ScreenSlidePagerAdapter(childFragmentManager, user.role!!.toRole()!!)
        pager.adapter = mPagerAdapter
        tlUserNavigationTabs.setupWithViewPager(pager)
    }

    override fun configureToolbar(toolbar: ToolbarController) {
        super.configureToolbar(toolbar)
        toolbar.setToolbarTitle("User")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_user_details, container, false) as ViewGroup
        return rootView
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidSupportInjection.inject(this)
        super.onCreate(savedInstanceState)
    }
}