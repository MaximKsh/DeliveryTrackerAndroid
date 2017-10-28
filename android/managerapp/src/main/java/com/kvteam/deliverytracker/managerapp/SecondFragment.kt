package com.kvteam.deliverytracker.managerapp

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kvteam.deliverytracker.core.ui.DeliveryTrackerFragment
import dagger.android.support.AndroidSupportInjection

class SecondFragment : DeliveryTrackerFragment() {
    //@Inject
    //lateinit var ss: ISimpleType

    //@Inject
    //lateinit var session: ISession

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_second, container, false)
    }


    override fun onAttach(activity: Activity?) {
        AndroidSupportInjection.inject(this)
        super.onAttach(activity)
    }

    override fun onCreate( savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onDetach() {
        super.onDetach()
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
    }


}// Required empty public constructor
