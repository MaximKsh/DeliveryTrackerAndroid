package com.kvteam.deliverytracker.core.dataprovider.base

import com.kvteam.deliverytracker.core.models.ModelBase
import com.kvteam.deliverytracker.core.models.ResponseReferencePackage
import com.kvteam.deliverytracker.core.webservice.IViewWebservice
import com.kvteam.deliverytracker.core.webservice.NetworkResult
import com.kvteam.deliverytracker.core.webservice.viewmodels.ViewResponse

abstract class BaseReferenceViewComponent<T : ModelBase>(
        viewWebservice: IViewWebservice,
        dataContainer: IDataContainer<T>) : BaseViewComponent<T>(viewWebservice, dataContainer) {

    protected open fun extractCollections(pack: ResponseReferencePackage, entry: T) {

    }

    override fun transformViewResultToEntries(viewResult: NetworkResult<ViewResponse>) : List<T> {
        return viewResult.entity?.viewResult?.map {
            val pack = ResponseReferencePackage()
            pack.fromMap(it)
            val entry = entryFactory()
            entry.fromMap(pack.entry)
            extractCollections(pack, entry)
            entry
        }?.toList()!!
    }
}