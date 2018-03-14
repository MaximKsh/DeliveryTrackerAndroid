package com.kvteam.deliverytracker.core.ui

import `in`.srain.cube.views.ptr.PtrDefaultHandler
import `in`.srain.cube.views.ptr.PtrFrameLayout
import `in`.srain.cube.views.ptr.PtrHandler
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.ActivityCompat.invalidateOptionsMenu
import android.support.v7.widget.LinearLayoutManager
import android.view.*
import com.kvteam.deliverytracker.core.R
import com.kvteam.deliverytracker.core.async.invokeAsync
import com.kvteam.deliverytracker.core.common.EMPTY_STRING
import com.kvteam.deliverytracker.core.common.ILocalizationManager
import com.kvteam.deliverytracker.core.ui.dropdowntop.DropdownTop
import com.kvteam.deliverytracker.core.ui.dropdowntop.DropdownTopItemInfo
import com.kvteam.deliverytracker.core.webservice.IViewWebservice
import dagger.android.support.AndroidSupportInjection
import eu.davidea.flexibleadapter.FlexibleAdapter
import kotlinx.android.synthetic.main.base_list.*
import javax.inject.Inject

abstract class BaseListFragment : DeliveryTrackerFragment() {
    private val selectedIndexKey = "selectedIndex"

    @Inject
    lateinit var viewWebservice: IViewWebservice

    @Inject
    lateinit var lm: ILocalizationManager

    abstract val viewGroup: String

    protected lateinit var mAdapter: FlexibleAdapter<*>

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidSupportInjection.inject(this)
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
    }

    abstract fun handleUpdateList (type: String, viewResult: List<Map<String, Any?>>)

    private fun updateList(viewName: String,
                           type: String?,
                           groupIndex: Int,
                           afterUpdate: (() -> Unit) = {}) {
        invokeAsync({
            viewWebservice.getViewResult(viewGroup, viewName)
        }, { result ->
            if (result.success && groupIndex == dropdownTop.lastSelectedIndex.get()) {
                handleUpdateList(type!!, result.entity!!.viewResult!!)
                afterUpdate()
                dropdownTop.update()
            }
        })
    }

    protected fun initAdapter() {
        mAdapter.setDisplayHeadersAtStartUp(true)
        rvBaseList.adapter = mAdapter
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.base_list, container, false)
    }

    private fun setCategories() {
        invokeAsync({
            viewWebservice.getDigest(viewGroup)
        }, { result ->
            if (result.success) {
                val digest = result.entity?.digest
                        ?.toList()
                        ?.sortedBy { it.second.order ?: Int.MAX_VALUE }!!

                val categoriesEnumeration = digest.map { category ->
                    DropdownTopItemInfo(
                            category.first,
                            category.second.entityType ?: EMPTY_STRING,
                            lm.getString(category.second.caption!!),
                            category.second.count!!.toInt(),
                            { index -> updateList(category.first, category.second.entityType, index) })
                }
                val categories = ArrayList(categoriesEnumeration)

                dropdownTop.updateDataSet(categories)
                val idx = dropdownTop.lastSelectedIndex.get()
                updateList(digest[idx].first, digest[idx].second.entityType, idx)
            }
        })
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        this.rvBaseList.layoutManager = LinearLayoutManager(
                this.activity?.applicationContext,
                LinearLayoutManager.VERTICAL,
                false)

        ptrFrame.setPtrHandler(object : PtrHandler {
            override fun onRefreshBegin(frame: PtrFrameLayout?) {
                val index = dropdownTop.lastSelectedIndex.get()
                val selectedItem = dropdownTop.items[index]
                updateList(selectedItem.viewName, selectedItem.entityType, index, {ptrFrame.refreshComplete()})
            }

            override fun checkCanDoRefresh(frame: PtrFrameLayout?, content: View?, header: View?): Boolean {
                return PtrDefaultHandler.checkContentCanBePulledDown(frame, content, header)
            }
        })

        val args = arguments
        if(args != null
            && args.containsKey(selectedIndexKey)) {
            dropdownTop.lastSelectedIndex.set(args.getInt(selectedIndexKey))
        } else {
            dropdownTop.lastSelectedIndex.set(0)
        }
        initAdapter()
        setCategories()
    }

    override fun onStop() {
        super.onStop()
        val args = arguments
        if(args == null) {
            val bundle = Bundle()
            bundle.putInt(selectedIndexKey, dropdownTop.lastSelectedIndex.get())
            arguments = bundle
        } else {
            args.putInt(selectedIndexKey, dropdownTop.lastSelectedIndex.get())
        }
    }

    private var menuItemsMask : Int = Int.MAX_VALUE

    protected fun setMenuMask(mask: Int) {
        menuItemsMask = mask
        activity?.invalidateOptionsMenu()
    }

    protected fun showMenuItem(item: Int) {
        menuItemsMask = menuItemsMask or item
        activity?.invalidateOptionsMenu()
    }

    protected fun hideMenuItem(item: Int) {
        menuItemsMask = menuItemsMask and item.inv()
        activity?.invalidateOptionsMenu()
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        for(i in 0 until menu.size()) {
            menu.getItem(i).isVisible = menuItemsMask and (1 shl i) != 0
        }
    }
}


