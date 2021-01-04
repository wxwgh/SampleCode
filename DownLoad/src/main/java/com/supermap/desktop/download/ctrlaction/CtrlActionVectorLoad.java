package com.supermap.desktop.download.ctrlaction;

import com.supermap.desktop.core.Interface.IBaseItem;
import com.supermap.desktop.core.implement.CtrlAction;
import com.supermap.desktop.download.ui.DialogVectorLoad;

public class CtrlActionVectorLoad extends CtrlAction {

    public CtrlActionVectorLoad(IBaseItem caller) {
        super(caller);
    }

    @Override
    public void run() {
        new DialogVectorLoad().showDialog();
    }
}
