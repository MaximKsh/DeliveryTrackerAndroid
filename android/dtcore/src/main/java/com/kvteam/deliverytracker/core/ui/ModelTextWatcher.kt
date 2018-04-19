package com.kvteam.deliverytracker.core.ui

import android.text.Editable
import android.text.TextWatcher
import com.kvteam.deliverytracker.core.dataprovider.base.DataProviderGetMode
import com.kvteam.deliverytracker.core.dataprovider.base.IDataComponent
import com.kvteam.deliverytracker.core.models.ModelBase
import java.util.*

open class ModelTextWatcher<T: ModelBase>(
        private val dataComponent: IDataComponent<T>,
        var id: UUID,
        private val updateFunc: (T, String) -> Unit
) : TextWatcher {
    override fun afterTextChanged(text: Editable?) {
        val model = dataComponent.get(id, DataProviderGetMode.DIRTY).entry
        updateFunc(model, text.toString())
    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
}
