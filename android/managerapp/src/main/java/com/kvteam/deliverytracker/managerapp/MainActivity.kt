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
import com.kvteam.deliverytracker.core.ui.DeliveryTrackerActivity
import com.kvteam.deliverytracker.managerapp.usersRecycleView.ManagersListFragment
import kotlinx.android.synthetic.main.toolbar.*


class MainActivity : DeliveryTrackerActivity() {
    lateinit var activeFragment: Fragment
    lateinit var mAddMenuItem: MenuItem
    lateinit var mRemoveMenuItem: MenuItem
    lateinit var mEditMenuItem: MenuItem
    var isInEditMode: Boolean = false

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
                this.toolbar_title.text = resources.getString(R.string.managers)
                val managersListFragment = ManagersListFragment()
                val transaction = supportFragmentManager.beginTransaction()
                transaction.add(R.id.mainContainer, managersListFragment)
                transaction.addToBackStack(null)
                transaction.commit()
                this.activeFragment = managersListFragment
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_performers -> {
                this.toolbar_title.text = resources.getString(R.string.performers)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_tasks -> {
                this.toolbar_title.text = resources.getString(R.string.tasks)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_my_tasks -> {
                this.toolbar_title.text = resources.getString(R.string.my_tasks)
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(this.toolbar_top)
        this.toolbar_title.text = resources.getString(R.string.managers)
        supportActionBar!!.setDisplayShowTitleEnabled(false)

        val managersListFragment = ManagersListFragment()
        val transaction = supportFragmentManager.beginTransaction()
        transaction.add(R.id.mainContainer, managersListFragment)
        transaction.addToBackStack(null)
        transaction.commit()
        this.activeFragment = managersListFragment

        removeShiftMode(navigation)

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        super.onPrepareOptionsMenu(menu)
        if (this.isInEditMode) {
            this.toolbar_left_action.text = resources.getString(R.string.cancel)
            mEditMenuItem.setVisible(false)
            mAddMenuItem.setVisible(true)
        } else if (!this.isInEditMode) {
            this.toolbar_left_action.text = ""
            mAddMenuItem.setVisible(false)
            mEditMenuItem.setVisible(true)
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_edit -> {
                this.isInEditMode = true
                val activeFragment = this.activeFragment
                if (activeFragment is ManagersListFragment) {
                    activeFragment.startEditMode()
                }
                this.invalidateOptionsMenu()
            }
            R.id.action_remove -> {
                this.isInEditMode = false
                val activeFragment = this.activeFragment
                if (activeFragment is ManagersListFragment) {
                    activeFragment.stopEditMode()
                }
                this.invalidateOptionsMenu()
            }
            R.id.action_add -> {

            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.toolbar_managers_tab_menu, menu)

        this.mAddMenuItem = menu.findItem(R.id.action_add)
        this.mEditMenuItem = menu.findItem(R.id.action_edit)
        this.mRemoveMenuItem = menu.findItem(R.id.action_remove)

        this.toolbar_left_action.setOnClickListener { _ ->
            this.toolbar_left_action.text = ""
            mAddMenuItem.setVisible(false)
            mEditMenuItem.setVisible(true)
            val activeFragment = this.activeFragment
            if (activeFragment is ManagersListFragment) {
                activeFragment.stopEditMode()
            }
        }

        return true
    }
}
