package com.kvteam.deliverytracker.core.ui.autocomplete

import android.content.Context
import android.util.AttributeSet
import android.view.View
import com.kvteam.deliverytracker.core.ui.GreatFocusListenerJavaHelper
import com.kvteam.deliverytracker.core.ui.maskededittext.IMaskedEditText
import com.kvteam.deliverytracker.core.ui.maskededittext.MaskedEditTextComponent
import com.kvteam.deliverytracker.core.ui.maskededittext.MaskedEditTextComponentAdapter
import android.os.Bundle
import android.os.Parcelable

class MaskedDelayAutocompleteTextView(context: Context, attrs: AttributeSet)
    : DelayAutoCompleteTextView(context, attrs), IMaskedEditText {

    private val onFocusSetter = GreatFocusListenerJavaHelper.onFocusChangeListenerSetter { listener: OnFocusChangeListener? ->
        super@MaskedDelayAutocompleteTextView.setOnFocusChangeListener(listener)
    }
    private val adapter = MaskedEditTextComponentAdapter(this)
    private val component: MaskedEditTextComponent? = MaskedEditTextComponent(adapter, context, attrs)

    override fun onSaveInstanceState(): Parcelable? {
        val superParcelable = super.onSaveInstanceState()
        val state = Bundle()
        state.putParcelable("super", superParcelable)
        component?.onSaveInstanceState(state)
        return state
    }

    override fun onRestoreInstanceState(state: Parcelable) {
        val bundle = state as Bundle
        super.onRestoreInstanceState(state.getParcelable("super"))
        component?.onRestoreInstanceState(bundle)
    }

    override fun setOnFocusChangeListener(listener: View.OnFocusChangeListener?) {
        GreatFocusListenerJavaHelper.addOnFocusChangeListener(
                this,
                onFocusSetter,
                listener)
    }

    override fun onSelectionChanged(selStart: Int, selEnd: Int) {
        component?.onSelectionChanged(selStart, selEnd)
        super.onSelectionChanged(selStart, selEnd)
    }

    override fun isKeepHint(): Boolean {
        return component != null && component.isKeepHint
    }

    override fun setKeepHint(keepHint: Boolean) {
        component?.isKeepHint = keepHint
    }

    override fun getMask(): String? {
        return component?.mask
    }

    override fun setMask(mask: String) {
        component?.mask = mask
    }

    override fun getRawText(): String? {
        return component?.rawText
    }

    override fun setCharRepresentation(charRepresentation: Char) {
        component?.charRepresentation = charRepresentation
    }

    override fun getCharRepresentation(): Char {
        return component?.charRepresentation ?: 0.toChar()
    }

}