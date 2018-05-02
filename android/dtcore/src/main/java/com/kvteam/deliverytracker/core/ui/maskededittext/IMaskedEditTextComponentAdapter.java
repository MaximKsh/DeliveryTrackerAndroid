package com.kvteam.deliverytracker.core.ui.maskededittext;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.TextView;

public interface IMaskedEditTextComponentAdapter {

    Editable getText();

    void setText(String string);

    void setText(CharSequence sequence);

    void addTextChangedListener(TextWatcher watcher);

    void setOnFocusChangeListener(View.OnFocusChangeListener listener);

    boolean hasFocus();

    void setSelection(int selection);

    void setSelection(int selStart, int selEnd);

    CharSequence getHint();

    int getCurrentHintTextColor();

    void setOnEditorActionListener(TextView.OnEditorActionListener l);

}
