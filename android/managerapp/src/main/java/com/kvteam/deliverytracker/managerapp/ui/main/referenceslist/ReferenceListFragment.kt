package com.kvteam.deliverytracker.managerapp.ui.main.referenceslist

import `in`.srain.cube.views.ptr.PtrDefaultHandler
import `in`.srain.cube.views.ptr.PtrFrameLayout
import `in`.srain.cube.views.ptr.PtrHandler
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.*
import com.kvteam.deliverytracker.core.async.invokeAsync
import com.kvteam.deliverytracker.core.common.EMPTY_STRING
import com.kvteam.deliverytracker.core.common.ILocalizationManager
import com.kvteam.deliverytracker.core.models.PaymentType
import com.kvteam.deliverytracker.core.ui.BaseListHeader
import com.kvteam.deliverytracker.core.ui.DeliveryTrackerFragment
import com.kvteam.deliverytracker.core.ui.IBaseListItemActions
import com.kvteam.deliverytracker.core.webservice.IReferenceWebservice
import com.kvteam.deliverytracker.core.webservice.IViewWebservice
import com.kvteam.deliverytracker.managerapp.R
import com.kvteam.deliverytracker.managerapp.ui.common.dropdowntop.DropdownTop
import com.kvteam.deliverytracker.managerapp.ui.common.dropdowntop.DropdownTopItemInfo
import com.kvteam.deliverytracker.managerapp.ui.main.MainActivity
import com.kvteam.deliverytracker.managerapp.ui.main.NavigationController
import dagger.android.support.AndroidSupportInjection
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.SelectableAdapter
import kotlinx.android.synthetic.main.fragment_reference_list.*
import javax.inject.Inject

open class ReferenceListFragment : DeliveryTrackerFragment() {
    @Inject
    lateinit var navigationController: NavigationController

    @Inject
    lateinit var viewWebservice: IViewWebservice

    @Inject
    lateinit var referenceWebservice: IReferenceWebservice

    @Inject
    lateinit var lm: ILocalizationManager

    private val referencesActions = object: IBaseListItemActions<ReferenceListItem> {
        override fun onDelete(adapter: FlexibleAdapter<*>, itemList: MutableList<ReferenceListItem>, item: ReferenceListItem) {
            if(adapter !is ReferenceListFlexibleAdapter) {
                return
            }
            invokeAsync({
                referenceWebservice.delete("PaymentType", item.reference.id!!)
            }, {
                if(it.success) {
                    itemList.remove(item)
                    adapter.updateDataSet(itemList, true)
                }
            })
        }

        override fun onItemClicked(adapter: FlexibleAdapter<*>, itemList: MutableList<ReferenceListItem>, item: ReferenceListItem) {}
    }

    private val dropDownTop: DropdownTop by lazy {
        (activity as MainActivity).dropDownTop
    }

    private lateinit var mAdapter: FlexibleAdapter<*>

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidSupportInjection.inject(this)
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_reference_list, container, false)
    }

    private fun formatReferences(viewResult: List<Map<String, Any?>>): MutableList<ReferenceListItem> {
        val header = BaseListHeader("A")

        return viewResult
                .map { referenceMap ->
                    val payment = PaymentType()
                    payment.fromMap(referenceMap)
                    ReferenceListItem(payment, header, lm)
                }.toMutableList()
    }

    private fun updateList(viewName: String,
                           type: String?,
                           groupIndex: Int,
                           afterUpdate: (() -> Unit) = {}) {
        invokeAsync({
            viewWebservice.getViewResult("ReferenceViewGroup", viewName)
        }, { result ->
            if (result.success && groupIndex == dropDownTop.lastSelectedIndex.get()) {
                when (type) {
                    "PaymentType" -> {
                        val referencesList = formatReferences(result.entity?.viewResult!!)
                        val adapter = mAdapter as? ReferenceListFlexibleAdapter
                        if (adapter != null) {
                            adapter.updateDataSet(referencesList, true)
                        } else {
                            mAdapter = ReferenceListFlexibleAdapter(referencesList, referencesActions)
                            initAdapter()
                        }
                    }
                }

                afterUpdate()
                dropDownTop.update()
            }
        })
    }

    private fun initAdapter() {
        mAdapter.mode = SelectableAdapter.Mode.SINGLE
        mAdapter.addListener(this)
        rvReferenceList.adapter = mAdapter
    }

    private fun setCategories() {
        invokeAsync({
            viewWebservice.getDigest("ReferenceViewGroup")
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
                            { index -> updateList(category.first, category.second.entityType, index)})
                }
                val categories = ArrayList(categoriesEnumeration)
                dropDownTop.updateDataSet(categories)

                updateList(digest[0].first, digest[0].second.entityType, 0)
            }
        })
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        this.rvReferenceList.layoutManager = LinearLayoutManager(
                this.activity?.applicationContext,
                LinearLayoutManager.VERTICAL,
                false)

        ptrFrame.setPtrHandler(object : PtrHandler {
            override fun onRefreshBegin(frame: PtrFrameLayout?) {
                val index = dropDownTop.lastSelectedIndex.get()
                val selectedItem = dropDownTop.items[index]
                updateList(selectedItem.viewName, selectedItem.entityType, index, {ptrFrame.refreshComplete()})
            }

            override fun checkCanDoRefresh(frame: PtrFrameLayout?, content: View?, header: View?): Boolean {
                return PtrDefaultHandler.checkContentCanBePulledDown(frame, content, header)
            }
        })

        // TODO: избавиться от лишней иницилизации
        mAdapter = ReferenceListFlexibleAdapter(mutableListOf(),referencesActions)
        initAdapter()
        setCategories()
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        if (savedInstanceState == null) {
            return
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_add -> {
                navigationController.navigateToAddPaymentReference()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.toolbar_reference_list_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }
}