package com.kvteam.deliverytracker.core.ui

import `in`.srain.cube.views.ptr.PtrDefaultHandler
import `in`.srain.cube.views.ptr.PtrFrameLayout
import `in`.srain.cube.views.ptr.PtrHandler
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import com.kvteam.deliverytracker.core.R
import com.kvteam.deliverytracker.core.async.launchUI
import com.kvteam.deliverytracker.core.common.EMPTY_STRING
import com.kvteam.deliverytracker.core.common.ILocalizationManager
import com.kvteam.deliverytracker.core.ui.errorhandling.IErrorHandler
import com.kvteam.deliverytracker.core.ui.toolbar.DropdownTopItemInfo
import com.kvteam.deliverytracker.core.ui.toolbar.ToolbarController
import com.kvteam.deliverytracker.core.webservice.IViewWebservice
import dagger.android.support.AndroidSupportInjection
import eu.davidea.flexibleadapter.FlexibleAdapter
import kotlinx.android.synthetic.main.base_list.*
import javax.inject.Inject

abstract class BaseListFragment : DeliveryTrackerFragment() {
    private val selectedIndexKey = "selectedIndex"
    private val searchTextKey = "searchTex"

    @Inject
    lateinit var viewWebservice: IViewWebservice

    @Inject
    lateinit var lm: ILocalizationManager

    @Inject
    lateinit var eh: IErrorHandler

    private var menuItemsMask : Int = Int.MAX_VALUE

    abstract val viewGroup: String

    protected lateinit var mAdapter: FlexibleAdapter<*>

    protected abstract fun handleUpdateList (type: String, viewResult: List<Map<String, Any?>>)



    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidSupportInjection.inject(this)
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.base_list, container, false)
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) = launchUI {
        super.onActivityCreated(savedInstanceState)
        rvBaseList.layoutManager = LinearLayoutManager(
                activity?.applicationContext,
                LinearLayoutManager.VERTICAL,
                false)

        ptrFrame.setPtrHandler(object : PtrHandler {
            override fun onRefreshBegin(frame: PtrFrameLayout?) = launchUI {
                if (dropdownTop.items.size != 0) {
                    val index = dropdownTop.lastSelectedIndex.get()
                    val selectedItem = dropdownTop.items[index]
                    updateList(selectedItem.viewName, selectedItem.entityType, index)
                }
                ptrFrame.refreshComplete()
            }

            override fun checkCanDoRefresh(frame: PtrFrameLayout?, content: View?, header: View?): Boolean {
                if(!PtrDefaultHandler.checkContentCanBePulledDown(frame, content, header)) {
                    return false
                }
                val frag = content as RecyclerView
                val adapter = frag.adapter as BaseListFlexibleAdapter<*, *, *>
                val viewHolders = adapter.getAllBoundViewHolders().toMutableSet()

                for(vh in viewHolders) {
                    if(vh is BaseListFlexibleAdapter.BaseListHolder
                        && !vh.swipeRevealLayout.isOpened
                        && !vh.swipeRevealLayout.isClosed) {
                        return false
                    }
                }

                return true
            }
        })

        dropdownTop.clearSearchText()
        val args = arguments
        if(args != null) {
            if(args.containsKey(selectedIndexKey))
                dropdownTop.lastSelectedIndex.set(args.getInt(selectedIndexKey))
            if(args.containsKey(searchTextKey))
                dropdownTop.searchText = args.getString(searchTextKey)
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
            bundle.putString(searchTextKey, dropdownTop.searchText)
            arguments = bundle
        } else {
            args.putInt(selectedIndexKey, dropdownTop.lastSelectedIndex.get())
            args.putString(searchTextKey, dropdownTop.searchText)
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        for(i in 0 until menu.size()) {
            menu.getItem(i).isVisible = menuItemsMask and (1 shl i) != 0
        }
    }

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

    protected open fun getViewFilterArguments(viewName: String,
                                              type: String?,
                                              groupIndex: Int,
                                              value: String) : Map<String, Any>? {
        return null
    }

    protected fun useSearchInToolbar(toolbar: ToolbarController) {
        toolbar.dropDownTop.showSearch({
            val index = dropdownTop.lastSelectedIndex.get()
            val selectedItem = dropdownTop.items[index]
            val args = getViewFilterArguments(
                    selectedItem.viewName,
                    selectedItem.entityType,
                    index,
                    it)
            updateList(
                    selectedItem.viewName,
                    selectedItem.entityType,
                    index,
                    arguments = args)
        }, {
            setCategories()
        })
    }

    protected fun initAdapter() {
        mAdapter.setDisplayHeadersAtStartUp(true)
        rvBaseList.adapter = mAdapter
    }

    private suspend fun updateList(viewName: String,
                           type: String?,
                           groupIndex: Int,
                           arguments: Map<String, Any>? = null) {
        val result = viewWebservice.getViewResultAsync(viewGroup, viewName, arguments)
        if(eh.handle(result)) {
            return
        }
        if (groupIndex == dropdownTop.lastSelectedIndex.get()) {
            handleUpdateList(type!!, result.entity!!.viewResult!!)
            dropdownTop.update()
        }
    }

    private suspend fun setCategories() {
        val result = viewWebservice.getDigestAsync(viewGroup)
        if(eh.handle(result)) {
            return
        }
        val digest = result.entity?.digest
                ?.toList()
                ?.sortedBy { it.second.order ?: Int.MAX_VALUE }!!

        val categoriesEnumeration = digest.map { category ->
            DropdownTopItemInfo(
                    category.first,
                    category.second.entityType ?: EMPTY_STRING,
                    lm.getString(category.second.caption!!),
                    category.second.count!!.toInt(),
                    { index ->
                        dropdownTop.clearSearchText()
                        updateList(category.first, category.second.entityType, index)
                    })
        }
        val categories = ArrayList(categoriesEnumeration)

        dropdownTop.updateDataSet(categories)
        val idx = dropdownTop.lastSelectedIndex.get()
        val prevSearchText = dropdownTop.searchText
        val args =
                if(prevSearchText.isNotBlank())
                    getViewFilterArguments(digest[idx].first, digest[idx].second.entityType, idx, prevSearchText)
                else null
        updateList(digest[idx].first, digest[idx].second.entityType, idx, arguments = args)
    }
}


