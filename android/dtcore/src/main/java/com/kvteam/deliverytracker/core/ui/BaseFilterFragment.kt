package com.kvteam.deliverytracker.core.ui

import com.kvteam.deliverytracker.core.dataprovider.base.DataProviderGetMode
import com.kvteam.deliverytracker.core.ui.toolbar.ToolbarController

abstract class BaseFilterFragment : BaseListFragment() {
    abstract val viewName: String
    abstract val type: String

    open fun getInitialArguments(): Map<String, Any>? = null

    override fun configureToolbar(toolbar: ToolbarController) {
        toolbar.disableDropDown()
        toolbarController.enableSearchMode( {
            updateList(
                    viewName,
                    type,
                    -1,
                    getViewFilterArguments(viewName, type, -1, it),
                    DataProviderGetMode.PREFER_WEB)
        },
                fragmentWide = true)
    }

    override suspend fun initializeAsync() {
        updateList(
                viewName,
                type,
                -1,
                getInitialArguments(),
                DataProviderGetMode.PREFER_WEB)
    }

    override fun onStop() {
        toolbarController.disableSearchMode()
        super.onStop()
    }
}