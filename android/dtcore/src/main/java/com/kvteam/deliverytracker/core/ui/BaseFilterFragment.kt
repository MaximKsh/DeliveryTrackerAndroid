package com.kvteam.deliverytracker.core.ui

import com.kvteam.deliverytracker.core.dataprovider.DataProviderGetMode
import com.kvteam.deliverytracker.core.ui.toolbar.ToolbarController

abstract class BaseFilterFragment : BaseListFragment() {
    abstract val viewName: String
    abstract val type: String

    override fun configureToolbar(toolbar: ToolbarController) {
        toolbar.disableDropDown()
    }

    override suspend fun initializeAsync() {
        updateList(
                viewName,
                type,
                -1,
                null,
                DataProviderGetMode.PREFER_WEB)
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

    override fun onDestroy() {
        super.onDestroy()
        toolbarController.disableSearchMode()
    }
}