package com.kvteam.deliverytracker.core.ui;

import android.view.View;
import android.widget.EditText;


public class GreatFocusListenerJavaHelper {
    public interface onFocusChangeListenerSetter {
        void setOnFocusChangeListener(View.OnFocusChangeListener listener);
    }


    private GreatFocusListenerJavaHelper() {
    }

    public static void addOnFocusChangeListener (
            EditText editText,
            onFocusChangeListenerSetter listenerSetter,
            View.OnFocusChangeListener listener) {
        if (listener == null) {
            listenerSetter.setOnFocusChangeListener(null);
            return;
        }

        View.OnFocusChangeListener oldListener = editText.getOnFocusChangeListener();
        if (oldListener instanceof GreatFocusListener) {
            GreatFocusListener greatOldListener = (GreatFocusListener) oldListener;
            greatOldListener.addListener(listener);
        } else {
            GreatFocusListener greatNewListener = new GreatFocusListener();
            if (oldListener != null) {
                greatNewListener.addListener(oldListener);
            }
            greatNewListener.addListener(listener);
            listenerSetter.setOnFocusChangeListener(greatNewListener);
        }
    }

    public static void removeOnFocusChangeListener(
            EditText editText,
            onFocusChangeListenerSetter listenerSetter,
            View.OnFocusChangeListener listener) {
        View.OnFocusChangeListener currentListener = editText.getOnFocusChangeListener();

        if (currentListener instanceof GreatFocusListener) {
            ((GreatFocusListener)currentListener).removeListener(listener);
        } else if (currentListener == null || currentListener == listener ) {
            listenerSetter.setOnFocusChangeListener(null);
        }
    }

}
