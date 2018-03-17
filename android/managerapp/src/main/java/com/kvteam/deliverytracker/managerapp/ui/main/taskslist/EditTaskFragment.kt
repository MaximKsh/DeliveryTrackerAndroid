package com.kvteam.deliverytracker.managerapp.ui.main.taskslist

import android.os.Bundle
import android.view.*
import android.widget.AdapterView
import com.kvteam.deliverytracker.core.async.invokeAsync
import com.kvteam.deliverytracker.core.common.IEventEmitter
import com.kvteam.deliverytracker.core.common.ILocalizationManager
import com.kvteam.deliverytracker.core.models.Product
import com.kvteam.deliverytracker.core.models.TaskInfo
import com.kvteam.deliverytracker.core.ui.DeliveryTrackerFragment
import com.kvteam.deliverytracker.core.ui.autocomplete.AutocompleteListAdapter
import com.kvteam.deliverytracker.core.ui.dropdowntop.ToolbarController
import com.kvteam.deliverytracker.core.webservice.ITaskWebservice
import com.kvteam.deliverytracker.core.webservice.IViewWebservice
import com.kvteam.deliverytracker.managerapp.R
import com.kvteam.deliverytracker.managerapp.ui.main.NavigationController
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_edit_task.*
import kotlinx.coroutines.experimental.runBlocking
import javax.inject.Inject

class EditTaskFragment : DeliveryTrackerFragment() {
    @Inject
    lateinit var navigationController: NavigationController

    @Inject
    lateinit var taskWebservice: ITaskWebservice

    @Inject
    lateinit var viewWebservice: IViewWebservice

    @Inject
    lateinit var emitter: IEventEmitter

    @Inject
    lateinit var lm: ILocalizationManager

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidSupportInjection.inject(this)
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        val selectedProduct = emitter.get("EditTaskFragment", "FilterProductSignal")
        if(selectedProduct != null
            && selectedProduct is Product) {
            val autocomplete = acvProductAutocomplete.autoCompleteTextView
            autocomplete.setText(selectedProduct.name)
        }
    }

    override fun configureToolbar(toolbar: ToolbarController) {
        toolbar.disableDropDown()
        toolbar.setToolbarTitle("Task")
        toolbar.showBackButton()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val autocomplete = acvProductAutocomplete.autoCompleteTextView
        autocomplete.setAutoCompleteDelay(200L)
        autocomplete.threshold = 2
        autocomplete.setAdapter(AutocompleteListAdapter(
                activity!!,
                {
                    runBlocking {
                        val networkResponse = viewWebservice.getViewResultAsync(
                                "ReferenceViewGroup",
                                "ProductsView",
                                mapOf("name" to it))
                        val viewResult = networkResponse.entity?.viewResult!!
                        val result = viewResult
                                .map { referenceMap ->
                                    val product = Product()
                                    product.fromMap(referenceMap)
                                    product
                                }.toMutableList()
                        result
                    }
                },
                { it.name!! }

        ))

        autocomplete.onItemClickListener = AdapterView.OnItemClickListener { av, it, pos, id ->
            val item = av.getItemAtPosition(pos) as Product
            autocomplete.setText(item.name)
        }

        acvProductAutocomplete.listSelectionButton.setOnClickListener {
            emitter.subscribe("EditTaskFragment", "FilterProductSignal")
            navigationController.navigateToFilterProducts()
        }

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_edit_task, container, false)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_finish -> {
                invokeAsync({
                    val task = TaskInfo()
                    taskWebservice.createAsync(task)
                }, {
                    if (it.success) {
                        navigationController.closeCurrentFragment()
                    }
                })
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.toolbar_edit_task_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }
}

