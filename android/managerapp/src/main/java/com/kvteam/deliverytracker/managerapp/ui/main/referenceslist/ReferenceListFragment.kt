package com.kvteam.deliverytracker.managerapp.ui.main.referenceslist

import android.os.Bundle
import android.view.*
import com.kvteam.deliverytracker.core.async.invokeAsync
import com.kvteam.deliverytracker.core.models.PaymentType
import com.kvteam.deliverytracker.core.ui.BaseListFragment
import com.kvteam.deliverytracker.core.ui.BaseListHeader
import com.kvteam.deliverytracker.core.ui.IBaseListItemActions
import com.kvteam.deliverytracker.core.webservice.IReferenceWebservice
import com.kvteam.deliverytracker.managerapp.R
import com.kvteam.deliverytracker.managerapp.ui.main.NavigationController
import eu.davidea.flexibleadapter.FlexibleAdapter
import javax.inject.Inject

open class ReferenceListFragment : BaseListFragment() {
    @Inject
    lateinit var navigationController: NavigationController

    @Inject
    lateinit var referenceWebservice: IReferenceWebservice

    override val viewGroup: String = "ReferenceViewGroup"

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

    private fun formatReferences(viewResult: List<Map<String, Any?>>): MutableList<ReferenceListItem> {
        val header = BaseListHeader("A")

        return viewResult
                .map { referenceMap ->
                    val payment = PaymentType()
                    payment.fromMap(referenceMap)
                    ReferenceListItem(payment, header, lm)
                }.toMutableList()
    }

    override fun handleUpdateList(type: String, viewResult: List<Map<String, Any?>>) {
        when (type) {
            "PaymentType" -> {
                val referencesList = formatReferences(viewResult)
                val adapter = mAdapter as? ReferenceListFlexibleAdapter
                if (adapter != null) {
                    adapter.updateDataSet(referencesList, true)
                } else {
                    mAdapter = ReferenceListFlexibleAdapter(referencesList, referencesActions)
                    initAdapter()
                }
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        mAdapter = ReferenceListFlexibleAdapter(mutableListOf(),referencesActions)
        super.onActivityCreated(savedInstanceState)
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