package com.kvteam.deliverytracker.managerapp

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import android.databinding.adapters.CompoundButtonBindingAdapter.setChecked
import android.support.annotation.RestrictTo
import android.support.design.internal.BottomNavigationItemView
import java.lang.reflect.AccessibleObject.setAccessible
import java.lang.reflect.Array.setBoolean
import android.support.design.internal.BottomNavigationMenuView
import android.support.v4.app.Fragment
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.kvteam.deliverytracker.core.ui.DeliveryTrackerActivity
import com.kvteam.deliverytracker.managerapp.usersRecycleView.ManagersListFragment
import kotlinx.android.synthetic.main.toolbar.*


class MainActivity : DeliveryTrackerActivity() {
    fun removeShiftMode(view: BottomNavigationView) {
        val menuView = view.getChildAt(0) as BottomNavigationMenuView
        try {
            val shiftingMode = menuView.javaClass.getDeclaredField("mShiftingMode")
            shiftingMode.isAccessible = true
            shiftingMode.setBoolean(menuView, false)
            shiftingMode.isAccessible = false
            for (i in 0 until menuView.childCount) {
                val item = menuView.getChildAt(i) as BottomNavigationItemView
                item.setShiftingMode(false)
                item.setChecked(item.itemData.isChecked)
            }
        } catch (e: NoSuchFieldException) {
            Log.e("ERROR NO SUCH FIELD", "Unable to get shift mode field")
        } catch (e: IllegalAccessException) {
            Log.e("ERROR ILLEGAL ALG", "Unable to change value of shift mode")
        }

    }

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_managers -> {
                val managersListFragment = ManagersListFragment()
                val transaction = supportFragmentManager.beginTransaction()
                transaction.add(R.id.mainContainer, managersListFragment)
                transaction.addToBackStack(null)
                transaction.commit()
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_performers -> {
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_tasks -> {
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_my_tasks -> {
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(this.toolbar_top)
        supportActionBar!!.setDisplayShowTitleEnabled(false)

        val transaction = supportFragmentManager.beginTransaction()
        transaction.add(R.id.mainContainer, ManagersListFragment())
        transaction.addToBackStack(null)
        transaction.commit()

        removeShiftMode(navigation)

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
    }
}
