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
import com.kvteam.deliverytracker.core.dataprovider.*
import com.kvteam.deliverytracker.core.models.*
import com.kvteam.deliverytracker.core.ui.errorhandling.IErrorHandler
import com.kvteam.deliverytracker.core.ui.toolbar.DropdownTopItemInfo
import com.kvteam.deliverytracker.core.ui.toolbar.ToolbarController
import com.kvteam.deliverytracker.core.webservice.IViewWebservice
import com.kvteam.deliverytracker.core.webservice.NetworkResult
import dagger.android.support.AndroidSupportInjection
import eu.davidea.flexibleadapter.FlexibleAdapter
import kotlinx.android.synthetic.main.base_list.*
import java.util.*
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

    @Inject
    lateinit var dp: DataProvider

    private var menuItemsMask : Int = Int.MAX_VALUE

    abstract val viewGroup: String

    abstract val tracer: FragmentTracer

    protected var mAdapter : FlexibleAdapter<*>
        get() = rvBaseList.adapter as FlexibleAdapter<*>
        set(value) {
            rvBaseList.adapter = value
        }

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
                    updateList(selectedItem.viewName, selectedItem.entityType, index, null, DataProviderGetMode.FORCE_WEB)
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
            setCategories(DataProviderGetMode.PREFER_WEB)
        })
    }

    protected open fun handleUsers(users: List<User>, animate: Boolean) {}

    protected open fun handleInvitations(invitations: List<Invitation>, animate: Boolean) {}

    protected open fun handleTasks(tasks: List<TaskInfo>, animate: Boolean) {}

    protected open fun handlePaymentTypes(paymentTypes: List<PaymentType>, animate: Boolean) {}

    protected open fun handleProducts(products: List<Product>, animate: Boolean) {}

    protected open fun handleClients(clients: List<Client>, animate: Boolean) {}

    protected open fun handleWarehouses(warehouses: List<Warehouse>, animate: Boolean) {}

    protected open fun handleErrorNetworkResult(result: NetworkResult<*>) {}

    private suspend fun updateList(
            viewName: String,
            type: String?,
            groupIndex: Int,
            arguments: Map<String, Any>? = null,
            getMode: DataProviderGetMode = DataProviderGetMode.PREFER_CACHE) {
        val mode = if(tracer.atTheBeginning()) DataProviderGetMode.PREFER_WEB else getMode
        try {
            when (type) {
                "User" -> {
                    loadTypedList(
                            dp.userViews,
                            dp.users,
                            groupIndex,
                            viewName,
                            arguments,
                            mode,
                            ::handleUsers)
                }
                "Invitation" -> {
                    loadTypedList(
                            dp.invitationView,
                            dp.invitations,
                            groupIndex,
                            viewName,
                            arguments,
                            mode,
                            ::handleInvitations)
                }
                "TaskInfo" -> {
                    loadTypedList(
                            dp.taskInfoViews,
                            dp.taskInfos,
                            groupIndex,
                            viewName,
                            arguments,
                            mode,
                            ::handleTasks)
                }
                "PaymentType" -> {
                    loadTypedList(
                            dp.paymentTypesViews,
                            dp.paymentTypes,
                            groupIndex,
                            viewName,
                            arguments,
                            mode,
                            ::handlePaymentTypes)
                }
                "Product" -> {
                    loadTypedList(
                            dp.productsViews,
                            dp.products,
                            groupIndex,
                            viewName,
                            arguments,
                            mode,
                            ::handleProducts)
                }
                "Client" -> {
                    loadTypedList(
                            dp.clientsViews,
                            dp.clients,
                            groupIndex,
                            viewName,
                            arguments,
                            mode,
                            ::handleClients)
                }
                "Warehouse" -> {
                    loadTypedList(
                            dp.warehousesViews,
                            dp.warehouses,
                            groupIndex,
                            viewName,
                            arguments,
                            mode,
                            ::handleWarehouses)
                }
            }
        } catch (e: NetworkException) {
            handleErrorNetworkResult(e.result)
        } catch (e: CacheException) {
            // TODO: обработка
        }
    }

    private suspend fun <T : ModelBase> loadTypedList(
            viewComponent: IViewComponent,
            dataComponent: IDataComponent<T>,
            groupIndex: Int,
            viewName: String,
            arguments: Map<String, Any>? = null,
            getMode: DataProviderGetMode,
            handle: (MutableList<T>, Boolean) -> Unit) {
        val (result, origin) = viewComponent.getViewResultAsync(viewGroup, viewName, arguments, getMode)
        if(getMode == DataProviderGetMode.PREFER_WEB
                || getMode == DataProviderGetMode.FORCE_WEB) {
            eh.handleNoInternetWarn(origin)
        }

        val entities = mutableListOf<T>()
        for(id in result) {
            try{
                val (e, _) = dataComponent.getAsync(id, DataProviderGetMode.FORCE_CACHE)
                entities.add(e)
            } catch (e: CacheException) {}
        }
        if (groupIndex == dropdownTop.lastSelectedIndex.get()) {
            handle(entities, origin == DataProviderGetOrigin.WEB)
            dropdownTop.update()
        }
    }

    private suspend fun setCategories(getMode: DataProviderGetMode = DataProviderGetMode.PREFER_CACHE) {
        val mode = if(tracer.atTheBeginning()) DataProviderGetMode.PREFER_WEB else getMode

        val result = try {
            dp.viewDigest.getDigestAsync(viewGroup, mode)
        } catch (e: NetworkException) {
            eh.handle(e.result)
            return
        }
        val digest = result
                .toList()
                .sortedBy { it.second.order ?: Int.MAX_VALUE }

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


