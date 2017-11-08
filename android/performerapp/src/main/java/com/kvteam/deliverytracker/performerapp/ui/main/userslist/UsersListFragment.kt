package com.kvteam.deliverytracker.performerapp.ui.main.userslist


import android.Manifest
import android.arch.lifecycle.ViewModelProviders
import android.content.pm.PackageManager
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kvteam.deliverytracker.core.models.UserModel
import com.kvteam.deliverytracker.core.ui.AutoClearedValue
import com.kvteam.deliverytracker.core.ui.DeliveryTrackerViewModelFactory

import com.kvteam.deliverytracker.performerapp.R
import com.kvteam.deliverytracker.performerapp.databinding.FragmentUsersListBinding
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject
import android.content.Intent
import android.net.Uri
import android.support.v4.content.ContextCompat.checkSelfPermission
import com.kvteam.deliverytracker.core.ui.DeliveryTrackerFragment
import kotlinx.android.synthetic.main.fragment_users_list.*


open class UsersListFragment : DeliveryTrackerFragment() {
    @Inject
    lateinit var vmFactory: DeliveryTrackerViewModelFactory

    lateinit var divider: DividerItemDecoration

    protected lateinit var binding: AutoClearedValue<FragmentUsersListBinding>
    protected lateinit var adapter: AutoClearedValue<UsersListAdapter>

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidSupportInjection.inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val dataBinding = DataBindingUtil.inflate<FragmentUsersListBinding>(
                inflater,
                R.layout.fragment_users_list,
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
                .get(UsersListViewModel::class.java)

        this.adapter = AutoClearedValue(
                this,
                UsersListAdapter(this::onCallClicked),
                {
                    it?.onCallClicked = null
                })
        binding.value?.viewModel = viewModel
        binding.value?.fragment = this
        binding.value?.performersList?.adapter = this.adapter.value
    }

    private fun onCallClicked(user: UserModel) {
        if(user.phoneNumber != null
                && checkSelfPermission(activity, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            val intent = Intent(Intent.ACTION_CALL)
            intent.data = Uri.parse("tel:${user.phoneNumber}")
            activity.startActivity(intent)
        }
    }
}
