package com.kvteam.deliverytracker.performerapp.ui.main.userslist

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.support.v4.content.ContextCompat.checkSelfPermission
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kvteam.deliverytracker.core.models.UserModel
import com.kvteam.deliverytracker.core.ui.AutoClearedValue
import com.kvteam.deliverytracker.core.ui.DeliveryTrackerFragment
import com.kvteam.deliverytracker.performerapp.R
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_users_list.*


open class UsersListFragment : DeliveryTrackerFragment() {
    protected val layoutManagerKey = "layoutManager"
    protected val usersListKey = "usersList"

    protected lateinit var adapter: AutoClearedValue<UsersListAdapter>

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidSupportInjection.inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
            inflater: LayoutInflater?,
            container: ViewGroup?,
            savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_users_list, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        this.rvUsersList.layoutManager = LinearLayoutManager(
                this.activity.applicationContext,
                LinearLayoutManager.VERTICAL,
                false)
        this.rvUsersList.addItemDecoration(
                DividerItemDecoration(this.context, DividerItemDecoration.VERTICAL))

        this.adapter = AutoClearedValue(
                this,
                UsersListAdapter(this::onCallClicked),
                {
                    it?.onCallClick = null
                })
        this.rvUsersList.adapter = this.adapter.value
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        if(outState == null) {
            return
        }
        outState.putParcelableArray(
                usersListKey,
                this.adapter.value?.items?.toTypedArray())
        outState.putParcelable(
                layoutManagerKey,
                this.rvUsersList.layoutManager.onSaveInstanceState())
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        if(savedInstanceState == null) {
            return
        }

        this.adapter.value?.items?.clear()
        this.adapter.value?.items?.addAll(
                savedInstanceState.getParcelableArray(usersListKey).map { it as UserModel })
        this.rvUsersList.layoutManager.onRestoreInstanceState(
                savedInstanceState.getParcelable(layoutManagerKey))
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
