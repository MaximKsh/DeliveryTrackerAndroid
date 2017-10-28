package com.kvteam.deliverytracker.core.ui

import android.arch.lifecycle.MutableLiveData
import android.support.design.widget.BottomNavigationView
import android.view.MenuItem

class DefaultNavigationItemSelectedListener(private val selectedItem: MutableLiveData<Int>)
    : BottomNavigationView.OnNavigationItemSelectedListener {
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        selectedItem.value = item.itemId
        return true
    }
}