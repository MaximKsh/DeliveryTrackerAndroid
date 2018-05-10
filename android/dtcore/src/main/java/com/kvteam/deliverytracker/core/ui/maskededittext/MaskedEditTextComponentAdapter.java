package com.kvteam.deliverytracker.core.ui.maskededittext;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.kvteam.deliverytracker.core.ui.GreatFocusListener;

public class MaskedEditTextComponentAdapter implements IMaskedEditTextComponentAdapter {

    private final EditText editText;

    public MaskedEditTextComponentAdapter(EditText editText) {
        this.editText = editText;
    }

    @Override
    public Editable getText () {
        return editText.getText();
    }

    @Override
    public void setText (String string){
        editText.setText(string);
    }

    @Override
    public void setText (CharSequence sequence){
        editText.setText(sequence);
    }

    @Override
    public void addTextChangedListener (TextWatcher watcher){
        editText.addTextChangedListener(watcher);
    }

    @Override
    public void setOnFocusChangeListener(View.OnFocusChangeListener listener){
        editText.setOnFocusChangeListener(listener);
    }

    @Override
    public boolean hasFocus () {
        return editText.hasFocus();
    }

    @Override
    public void setSelection ( int selection){
        editText.setSelection(selection);
    }

    @Override
    public void setSelection (
    int selStart,
    int selEnd){
        editText.setSelection(selStart, selEnd);
    }

    @Override
    public CharSequence getHint () {
        return editText.getHint();
    }

    @Override
    public int getCurrentHintTextColor () {
        return editText.getCurrentHintTextColor();
    }

    @Override
    public void setOnEditorActionListener (TextView.OnEditorActionListener l){
        editText.setOnEditorActionListener(l);
    }
}
