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
import com.kvteam.deliverytracker.core.models.User
import com.kvteam.deliverytracker.core.ui.AutoClearedValue
import com.kvteam.deliverytracker.core.ui.DeliveryTrackerFragment
import com.kvteam.deliverytracker.performerapp.R
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_users_list.*


open class UsersListFragment : DeliveryTrackerFragment() {
    protected val layoutManagerKey = "layoutManager"
    protected val usersListKey = "usersList"

    protected lateinit var adapter: AutoClearedValue<UsersListAdapter>
    protected var ignoreSavedState = false

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidSupportInjection.inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_users_list, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        rvUsersList.layoutManager = LinearLayoutManager(
                activity!!.applicationContext,
                LinearLayoutManager.VERTICAL,
                false)
        rvUsersList.addItemDecoration(
                DividerItemDecoration(context, DividerItemDecoration.VERTICAL))

        adapter = AutoClearedValue(
                this,
                UsersListAdapter(this::onCallClicked, { context!!.getString(it) }),
                {
                    it?.onCallClick = null
                    it?.getLocalizedString = null
                })
        rvUsersList.adapter = adapter.value

        savedInstanceState?.apply {
            val adapter = adapter.value
            val layoutManager = rvUsersList?.layoutManager
            if(adapter != null
                    && layoutManager != null) {
                if(containsKey(usersListKey)
                        && containsKey(layoutManagerKey)) {
                    adapter.items.clear()
                    layoutManager.onRestoreInstanceState(getParcelable(layoutManagerKey))
                } else {
                    ignoreSavedState = true
                }
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.apply {
            val adapter = adapter.value
            val layoutManager = rvUsersList?.layoutManager
            if(adapter != null
                    && layoutManager != null) {
                putParcelable(
                        layoutManagerKey,
                        layoutManager.onSaveInstanceState())
            }

        }
    }

    private fun onCallClicked(user: User) {
        if(user.phoneNumber != null
                && checkSelfPermission(activity!!, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            val intent = Intent(Intent.ACTION_CALL)
            intent.data = Uri.parse("tel:${user.phoneNumber}")
            activity!!.startActivity(intent)
        }
    }
}
