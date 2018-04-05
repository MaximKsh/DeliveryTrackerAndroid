package com.kvteam.deliverytracker.managerapp.ui.main.taskslist

import android.view.ViewGroup
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import com.kvteam.deliverytracker.core.ui.DeliveryTrackerFragment
import com.kvteam.deliverytracker.managerapp.R


class TaskNumberAndDetailsFragment : DeliveryTrackerFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_task_number_and_details, container, false) as ViewGroup
        return rootView
    }
}
