package com.kvteam.deliverytracker.core.ui.maskededittext;

import android.os.Bundle;

public interface IMaskedEditText {
    boolean isKeepHint();

    void setKeepHint(boolean keepHint);

    String getMask();

    void setMask(String mask);

    String getRawText();

    void setCharRepresentation(char charRepresentation);

    char getCharRepresentation();
}
