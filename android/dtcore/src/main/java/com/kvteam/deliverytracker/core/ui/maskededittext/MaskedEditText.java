package com.kvteam.deliverytracker.core.ui.maskededittext;

import android.content.Context;
import android.support.v7.widget.AppCompatEditText;
import android.util.AttributeSet;

import com.kvteam.deliverytracker.core.ui.GreatFocusListenerJavaHelper;


public class MaskedEditText extends AppCompatEditText implements IMaskedEditText{

    private final GreatFocusListenerJavaHelper.onFocusChangeListenerSetter listenerSetter =
            new GreatFocusListenerJavaHelper.onFocusChangeListenerSetter() {

                @Override
                public void setOnFocusChangeListener(OnFocusChangeListener listener) {
                    MaskedEditText.super.setOnFocusChangeListener(listener);
                }
            };

    private final IMaskedEditTextComponentAdapter adapter =
            new MaskedEditTextComponentAdapter(this);

    private final MaskedEditTextComponent component;

    public MaskedEditText(Context context) {
		super(context);
		component = new MaskedEditTextComponent(adapter, context);
	}

	public MaskedEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		component = new MaskedEditTextComponent(adapter, context, attrs);

    }

    public MaskedEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        component = new MaskedEditTextComponent(adapter, context);
    }
//
//	@Override
//	public Parcelable onSaveInstanceState() {
//		final Parcelable superParcellable = super.onSaveInstanceState();
//		final Bundle state = new Bundle();
//		state.putParcelable("super", superParcellable);
//		state.putString("text", getRawText());
//		state.putBoolean("keepHint", isKeepHint());
//		return state;
//	}
//
//	@Override
//	public void onRestoreInstanceState(Parcelable state) {
//		Bundle bundle = (Bundle) state;
//		keepHint = bundle.getBoolean("keepHint", false);
//		super.onRestoreInstanceState(((Bundle) state).getParcelable("super"));
//		final String text = bundle.getString("text");
//
//		setText(text);
//		Log.d(TAG, "onRestoreInstanceState: " + text);
//	}

	/** @param listener - its onFocusChange() method will be called before performing MaskedEditText operations,
	 * related to this event. */
	@Override
	public void setOnFocusChangeListener(OnFocusChangeListener listener) {
        GreatFocusListenerJavaHelper.addOnFocusChangeListener(this, listenerSetter, listener);
	}

	@Override
	protected void onSelectionChanged(int selStart, int selEnd) {
		if (component != null) {
		    component.onSelectionChanged(selStart, selEnd);
        }
		super.onSelectionChanged(selStart, selEnd);
	}

    @Override
    public boolean isKeepHint() {
        return component != null && component.isKeepHint();
    }

    @Override
    public void setKeepHint(boolean keepHint) {
        if (component != null) {
            component.setKeepHint(keepHint);
        }
    }

    @Override
    public String getMask() {
        if (component != null) {
            return component.getMask();
        }
        return null;
    }

    @Override
    public void setMask(String mask) {
	    if (component != null) {
	        component.setMask(mask);
        }
    }

    @Override
    public String getRawText() {
        if (component != null) {
            return component.getRawText();
        }
        return null;
    }

    @Override
    public void setCharRepresentation(char charRepresentation) {
        if (component != null) {
            component.setCharRepresentation(charRepresentation);
        }
    }

    @Override
    public char getCharRepresentation() {
        if (component != null ){
            return component.getCharRepresentation();
        }
        return 0;
    }
}
